package com.service.sport_companion.api.service;

import com.service.sport_companion.domain.model.dto.request.auth.SignUpDto;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

  // 카카오 회원가입 or 로그인
  ResultResponse oAuthForKakao(String code, HttpServletResponse response);

  // 닉네임 중복 확인
  ResultResponse checkNickname(String nickname);

  // 회원가입 추가 데이터 저장
  ResultResponse signup(SignUpDto signUpDto);

  // Jwt 토큰 재발급 로직
  ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response);
}
