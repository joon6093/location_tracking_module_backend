package org.changppo.tracking.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.jwt.exception.JwtNotExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationManager authenticationManager;
    private final RequestMatcher requestMatcher = new RequestHeaderRequestMatcher(HttpHeaders.AUTHORIZATION);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String tokenValue = resolveToken(request).orElseThrow(JwtNotExistException::new);
            JwtAuthenticationToken token = new JwtAuthenticationToken(tokenValue); // 인증되지 않은 토큰
            Authentication authentication = this.authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("JWT 로부터 인증정보를 만드는데 실패했습니다: {}", e.getMessage());
            request.setAttribute("exception", e); // 발생한 exception 을 request 에 추가
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }
}
