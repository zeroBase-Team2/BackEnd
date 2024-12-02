package com.service.sport_companion.api.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.sport_companion.domain.model.type.TokenType;
import com.service.sport_companion.domain.model.type.UrlType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 접근 권한 허용 경로 검증
    if (isAllowedRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Header에서 Access-Token 추출
    String tokenHeader = request.getHeader(TokenType.ACCESS.getValue());

    // Bearer 시작 여부 및 null 값 검증
    if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
      log.info("Token is not valid");
      setResponse(response);
      return;
    }

    String accessToken = tokenHeader.substring(7);

    // Access-Token 만료시간 검증
    if (jwtUtil.isExpired(accessToken)) {
      log.info("Access Token 만료! ");
      setResponse(response);
      return;
    }

    setAuthentication(accessToken);
    filterChain.doFilter(request, response);
  }

  // userId를 추출 및 인증 정보를 설정
  private void setAuthentication(String token) {
    Long userId = jwtUtil.getUserId(token);

    Authentication authentication = jwtUtil.getAuthentication(userId.toString());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  // Access-Token 만료시 403 반환
  private void setResponse(HttpServletResponse response) throws IOException {
    log.info("setResponse");
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // CORS 헤더 추가
    response.setHeader("Access-Control-Allow-Origin", UrlType.FRONT_VERCEL_URL.getUrl());
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,DELETE,TRACE,OPTIONS,PATCH,PUT");
    response.setHeader("Access-Control-Allow-Headers", "*");
    response.setHeader("Access-Control-Expose-Headers", "access, Location");

    Map<String, String> body = Map.of("message", "유효하지 않은 Access Token");
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

  // 접근 권한 허용 경로 검증
  private boolean isAllowedRequest(HttpServletRequest request) {
    AntPathMatcher pathMatcher = new AntPathMatcher();
    String method = request.getMethod();
    String path = request.getRequestURI();

    // 1. Options 요청 검증
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return true;
    }

    log.info("request uri >>> {}", path);
    // 2. 예외 경로 검증
    if (isExcludedPath(pathMatcher, path)) {
      return true;
    }

    // 3. 요청 메서드 검증
    if ("GET".equalsIgnoreCase(method)) {

      // 4. 토큰 검증
      if (request.getHeader(TokenType.ACCESS.getValue()) == null) {

        // 5. 비 로그인 사용자 요청 경로 검증
        return isMatchingGetRequest(pathMatcher, path);
      }
    }

    return false;
  }

  // Jwt 검증 제외 경로
  private boolean isExcludedPath(AntPathMatcher pathMatcher, String requestURI) {

    return pathMatcher.match("/", requestURI)
        || pathMatcher.match("/api/v1/auth/**", requestURI)
        || pathMatcher.match("/api/v1/clubs/**", requestURI)
        || pathMatcher.match("/api/v1/news/**", requestURI)
        || pathMatcher.match("/api/v1/fixture/crawl", requestURI)
        || pathMatcher.match("/v3/api-docs/**", requestURI)
        || pathMatcher.match("/swagger-ui/**", requestURI)
        || pathMatcher.match("/swagger-ui.html", requestURI)
        || pathMatcher.match("/swagger-resources/**", requestURI);
  }

  public boolean isMatchingGetRequest(AntPathMatcher pathMatcher, String requestURI) {

    return pathMatcher.match("/api/v1/fixture", requestURI)
        || pathMatcher.match("/api/v1/topic/top5", requestURI);
  }
}
