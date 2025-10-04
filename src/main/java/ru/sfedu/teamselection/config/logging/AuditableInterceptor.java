package ru.sfedu.teamselection.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.sfedu.teamselection.component.CachedBodyHttpServletRequest;
import ru.sfedu.teamselection.component.CachedBodyHttpServletResponse;
import ru.sfedu.teamselection.service.audit.AuditService;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuditableInterceptor implements HandlerInterceptor {
    private final AuditService auditService;

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = handlerMethod.getMethodAnnotation(Auditable.class);
            if (annotation != null) {
                CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

                AuditDetails auditDetails = getAuditDetails((OAuth2AuthenticationToken) request.getUserPrincipal());
                request.setAttribute(AUDIT_POINT, annotation.auditPoint());
                UUID traceId = UUID.randomUUID();
                request.setAttribute(TRACE_ID, traceId);

                CompletableFuture.runAsync(() -> {
                    try {
                        String auditPoint = annotation.auditPoint() + REQUEST_POSTFIX;
                        String payload = isReadableContent(request)
                                ? getRequestBody(cachedRequest)
                                : NOT_SERIALIZABLE_PLACEHOLDER;

                        auditService.log(
                                traceId,
                                auditDetails,
                                auditPoint,
                                request.getRemoteAddr(),
                                payload
                        );
                    } catch (Exception ex) {
                        log.warn("Error while saving audit for request {} {}", traceId, request.getRequestURI());
                    }
                });
            } else {
                log.warn("Skip saving audit for request {}, method is not annotated", request.getRequestURI());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            @Nullable Exception ex
    ) {
        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = handlerMethod.getMethodAnnotation(Auditable.class);

            if (annotation != null) {
                AuditDetails auditDetails = getAuditDetails((OAuth2AuthenticationToken) request.getUserPrincipal());

                var cachedResponse = (CachedBodyHttpServletResponse) response;


                    try {
                        UUID traceId = (UUID) request.getAttribute(TRACE_ID);

                        String auditPoint = annotation.auditPoint() + RESPONSE_POSTFIX;
                        String payload = isReadableResponse(response)
                                ? cachedResponse.getCachedContentAsString()
                                : NOT_SERIALIZABLE_PLACEHOLDER;

                        auditService.log(
                                traceId,
                                auditDetails,
                                auditPoint,
                                null,
                                payload
                        );
                    } catch (Exception e) {
                        log.error("Error logging response: {}", e.getMessage());
                    }
               // });
            } else {
                log.warn("Not saving audit - response code is {}", response.getStatus());
            }
        }
    }

    private String getRequestBody(CachedBodyHttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "[UNABLE TO READ BODY]";
        }
    }

    private boolean isReadableContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return true;
        }

        // Не логируем бинарный контент
        return !contentType.contains("multipart/form-data")
                && !contentType.contains("octet-stream")
                && !contentType.contains("image/")
                && !contentType.contains("video/");
    }

    private boolean isReadableResponse(HttpServletResponse response) {
        String contentType = response.getHeader("Content-Type");
        if (contentType == null) {
            return true;
        }

        return contentType.equals("application/json");
    }

    private AuditDetails getAuditDetails(OAuth2AuthenticationToken token) {
        return new AuditDetails(
                token.getPrincipal().getAttribute("email").toString()
        );
    }

    static final String AUDIT_POINT = "auditPoint";
    static final String TRACE_ID = "traceId";
    private static final String REQUEST_POSTFIX = ".Request";
    private static final String RESPONSE_POSTFIX = ".Response";
    private static final String NOT_SERIALIZABLE_PLACEHOLDER = "{\"error\": \"__NOT_SERIALIZABLE__\"}";

    public record AuditDetails(
            String userEmail
    ) {}
}
