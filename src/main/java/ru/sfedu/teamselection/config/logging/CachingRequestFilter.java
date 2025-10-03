package ru.sfedu.teamselection.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sfedu.teamselection.component.CachedBodyHttpServletRequest;
import ru.sfedu.teamselection.component.CachedBodyHttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class CachingRequestFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain chain
    ) throws IOException, ServletException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        CachedBodyHttpServletResponse cachedResponse = new CachedBodyHttpServletResponse(response);

        chain.doFilter(cachedRequest, cachedResponse);
    }
}
