package ru.sfedu.teamselection.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.sfedu.teamselection.component.CachedBodyHttpServletRequest;
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
                AuditDetails auditDetails = getAuditDetails((OAuth2AuthenticationToken) request.getUserPrincipal());
                request.setAttribute(AUDIT_POINT, annotation.auditPoint());

                CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
                if (isReadableContent(request)) {
                    String body = getRequestBody(cachedRequest);
                    auditService.log(
                        auditDetails,
                        annotation.auditPoint() + REQUEST_POSTFIX,
                        request.getRemoteAddr(),
                        body
                    );
                } else {
                    auditService.log(
                            auditDetails,
                            annotation.auditPoint(),
                            request.getRemoteAddr(),
                            "__NOT_READABLE__"
                    );
                }

            }
        }
        return true;
    }

    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            @Nullable ModelAndView modelAndView
    ) {
        if (handler instanceof HandlerMethod handlerMethod) {
            var annotation = handlerMethod.getMethodAnnotation(Auditable.class);

            if (annotation != null) {
                AuditDetails auditDetails = getAuditDetails((OAuth2AuthenticationToken) request.getUserPrincipal());

//                auditService.log(
//                        auditDetails,
//                        annotation.auditPoint(),
//                        request.getRemoteAddr(),
//                        payload
//                );
            } else {
                log.warn("Not sending audit - response code is " + response.getStatus());
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

    private AuditDetails getAuditDetails(OAuth2AuthenticationToken token) {
        return new AuditDetails(
                token.getPrincipal().getAttribute("email").toString()
        );
    }

    static final String AUDIT_POINT = "auditPoint";
    private static final String REQUEST_POSTFIX = ".Request";
    private static final String RESPONSE_POSTFIX = ".Response";

    public record AuditDetails(
            String userEmail
    ) {}
}
