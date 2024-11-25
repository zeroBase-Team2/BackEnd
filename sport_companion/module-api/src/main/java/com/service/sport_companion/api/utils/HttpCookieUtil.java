package com.service.sport_companion.api.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class HttpCookieUtil {

  public static void addCookieToResponse(HttpServletResponse response, String key, String value, long maxAge) {
    String cookie =  ResponseCookie.from(key, value)
        .httpOnly(true)         // HttpOnly 설정
        .secure(true)           // Secure 설정 (HTTPS 환경 필수)
        .path("/")              // 쿠키 경로 설정
        .maxAge(maxAge)         // 유효 시간 (초 단위)
        .sameSite("None")       // SameSite 설정 (Cross-Origin 허용)
        .build().toString();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie);
  }


  public static String getCookieValue(HttpServletRequest request, String cookieName) {
    if (request.getCookies() == null) {
      return null; // 쿠키가 없는 경우 null 반환
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }
}
