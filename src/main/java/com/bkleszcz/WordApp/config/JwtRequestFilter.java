package com.bkleszcz.WordApp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PM = new AntPathMatcher();

    /** Uwaga: BEZ /api — ścieżki względne do context-path (zadziała i z /api, i bez). */
    private static final String[] PUBLIC = {
            "auth/**",
            "UserApi/create",
            "UserApi/loggedUser",
            "guess/random",
            "guess/check",
            "dictionary/**",
            "rank/**",
            "statistics/**",
            "error",
            "api/auth/**",
            "api/UserApi/create",
            "api/UserApi/loggedUser",
            "api/guess/random",
            "api/guess/check",
            "api/dictionary/**",
            "api/rank/**",
            "api/statistics/**",
            "get/sentences"
    };

    private final JwtUtil jwtUtil;
    private final @Lazy UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1) Preflight zawsze wolny
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 2) PUBLIC bez autoryzacji JWT
        String sp = request.getServletPath();             // np. "/rank/top"
        String path = sp.startsWith("/") ? sp.substring(1) : sp;  // "rank/top"
        for (String p : PUBLIC) {
            if (PM.match(p, path)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 3) Jeżeli brak nagłówka Authorization -> NIE rób 403, przepuść dalej
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, username)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                ctx.setAuthentication(authToken);
                SecurityContextHolder.setContext(ctx);
            }
        }

        chain.doFilter(request, response);
    }
}
