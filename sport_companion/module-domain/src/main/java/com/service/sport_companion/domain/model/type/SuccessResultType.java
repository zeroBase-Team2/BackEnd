package com.service.sport_companion.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessResultType {

  // Auth
  SUCCESS_SIGNUP_REQUIRED(HttpStatus.OK, "회원가입이 필요한 회원입니다."),
  SUCCESS_SIGNUP(HttpStatus.CREATED, "회원가입 성공!"),
  SUCCESS_LOGIN(HttpStatus.OK, "로그인 성공!"),
  AVAILABLE_NICKNAME(HttpStatus.OK, "사용 가능한 닉네임입니다."),
  UNAVAILABLE_NICKNAME(HttpStatus.OK, "이미 사용 중인 닉네임입니다."),
  SUCCESS_REISSUE_TOKEN(HttpStatus.OK, "Access 토큰 재발급 성공."),

  //Club
  SUCCESS_GET_ALL_CLUBS_LIST(HttpStatus.OK, "모든 구단 조회 성공!"),

  ;

  private final HttpStatus status;
  private final String message;
}
