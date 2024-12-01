package com.service.sport_companion.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FailedResultType {

  // Auth
  EMAIL_ALREADY_USED(HttpStatus.BAD_REQUEST, "가입 이력이 있는 이메일 입니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원 입니다."),
  PROVIDER_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 ProviderId로 회원을 찾을 수 없습니다."),
  COOKIE_IS_NULL(HttpStatus.FORBIDDEN, "쿠키 값이 존재하지 않습니다."),
  REFRESH_TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다!"),

  // API
  UNIQUE_NICKNAME_FAILED(HttpStatus.TOO_MANY_REQUESTS, "닉네임 생성에 실패했습니다."),
  UNIQUE_NICKNAME_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "닉네임 생성 중 문제가 발생했습니다."),
  ACCESS_TOKEN_RETRIEVAL(HttpStatus.UNAUTHORIZED, "액세스 토큰을 가져오는 데 실패했습니다."),
  USER_INFO_RETRIEVAL(HttpStatus.UNAUTHORIZED, "사용자 정보를 가져오는 데 실패했습니다."),

  // Crawling
  MBC_NEWS_PARSING_FAILED(HttpStatus.BAD_GATEWAY, "MBC 뉴스 파싱에 실패했습니다."),

  // CustomTopic
  CUSTOM_TOPIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 주제입니다."),
  DELETE_TOPIC_FORBIDDEN(HttpStatus.FORBIDDEN, "주제 삭제 권한이 없습니다"),
  ALREADY_RECOMMEND_TOPIC(HttpStatus.BAD_REQUEST, "이미 추천한 주제입니다."),

  // Fixture
  FIXTURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "데이터가 없습니다."),

  // Sport
  SPORT_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 종목입니다."),

  // ReservationSite
  RESERVATION_SITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 예매 사이트 입니다."),

  ;

  private final HttpStatus status;
  private final String message;
}
