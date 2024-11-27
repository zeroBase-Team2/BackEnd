package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.auth.jwt.JwtUtil;
import com.service.sport_companion.api.auth.oauth.KakaoAuthHandler;
import com.service.sport_companion.api.component.ClubsHandler;
import com.service.sport_companion.api.component.RedisHandler;
import com.service.sport_companion.api.component.SupportedClubsHandler;
import com.service.sport_companion.api.component.UserHandler;
import com.service.sport_companion.api.service.AuthService;
import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.SignUpDataEntity;
import com.service.sport_companion.domain.entity.SupportedClubsEntity;
import com.service.sport_companion.domain.entity.UsersEntity;
import com.service.sport_companion.domain.model.auth.KakaoUserDetailsDTO;
import com.service.sport_companion.domain.model.dto.request.auth.SignUpDto;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.auth.SignUpData;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import com.service.sport_companion.domain.model.type.TokenType;
import com.service.sport_companion.domain.model.type.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final KakaoAuthHandler kakaoAuthHandler;
  private final UserHandler userHandler;
  private final RedisHandler redisHandler;
  private final ClubsHandler clubsHandler;
  private final SupportedClubsHandler supportedClubsHandler;
  private final JwtUtil jwtUtil;


  @Override
  public ResultResponse oAuthForKakao(String code, HttpServletResponse response) {
    // "인가 코드"로 "액세스 토큰" 요청
    String accessToken = kakaoAuthHandler.getAccessToken(code);

    // 토큰으로 사용자 정보 요청
    KakaoUserDetailsDTO userInfo = kakaoAuthHandler.getUserDetails(accessToken);

    // 회원 가입 여부 확인
    UsersEntity user = userHandler.findUserByUserInfo(userInfo);

    // 가입 이력이 없는 경우 추가 데이터 입력을 위해 회원정보 전달
    if(user == null) {
      String nickname = userHandler.getRandomNickname(1);
      SignUpData signUpData = new SignUpData(userInfo.getProviderId(), nickname);

      userHandler.saveSingUpCacheData(userInfo);

      return new ResultResponse(SuccessResultType.SUCCESS_SIGNUP_REQUIRED, signUpData);
    }

    // 로그인 성공 : Access, Refresh 토큰 헤더로 전달
    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    String refresh = jwtUtil.createJwt("refresh", user.getUserId(), user.getRole());

    response.addHeader(TokenType.ACCESS.getValue(), access);
    response.addHeader(TokenType.REFRESH.getValue(), refresh);

    return ResultResponse.of(SuccessResultType.SUCCESS_LOGIN);
  }


  // 닉네임 중복 검증
  @Override
  public ResultResponse checkNickname(String nickname) {
    boolean isValidNickname = !userHandler.existsByNickname(nickname);

    SuccessResultType resultType = (isValidNickname) ?
      SuccessResultType.AVAILABLE_NICKNAME : SuccessResultType.UNAVAILABLE_NICKNAME;

    return new ResultResponse(resultType, isValidNickname);
  }

  // 회원가입
  @Override
  public ResultResponse signup(SignUpDto signUpDto) {
    // 1. Redis에서 사용자 데이터 가져오기
    SignUpDataEntity signUpData = redisHandler.getSignUpDataByProviderId(signUpDto.getProviderId());

    // 2. 사용자 생성 및 저장
    UsersEntity user = createUser(signUpData, signUpDto.getNickname());
    userHandler.saveUser(user);

    // 3. 선호 구단 처리
    saveSupportedClub(user, signUpDto.getClubName());

    return ResultResponse.of(SuccessResultType.SUCCESS_SIGNUP);
  }

  // 사용자 생성 및 저장
  private UsersEntity createUser(SignUpDataEntity signUpData, String nickname) {
    return UsersEntity.builder()
        .email(signUpData.getEmail())
        .nickname(nickname)
        .provider(signUpData.getProvider())
        .providerId(signUpData.getProviderId())
        .role(UserRole.USER)
        .createdAt(LocalDateTime.now())
        .build();
  }

  // 선호 구단 처리
  private void saveSupportedClub(UsersEntity user, String clubName) {
    ClubsEntity club = clubsHandler.findByClubName(clubName);

    SupportedClubsEntity supportedClub = SupportedClubsEntity.builder()
        .user(user)
        .club(club)
        .build();

    supportedClubsHandler.saveSupportedClub(supportedClub);
  }

  @Override
  public ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();

    // 쿠키 검증
    if (cookies == null || cookies.length == 0) {
      log.info("cookie is empty");
      throw new GlobalException(FailedResultType.COOKIE_IS_NULL);
    }

    log.info("cookies.length -> {}", cookies.length);

    // refresh-token 추출
    String refreshToken =  Arrays.stream(request.getCookies())
        .filter(cookie -> TokenType.REFRESH.getValue().equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);

    // refresh-token 검증
    if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
      throw new GlobalException(FailedResultType.REFRESH_TOKEN_IS_EXPIRED);
    }

    Long userId = jwtUtil.getUserId(refreshToken);
    UsersEntity user = userHandler.findByUserId(userId);

    String access = jwtUtil.createJwt("access", user.getUserId(), user.getRole());
    response.addHeader(TokenType.ACCESS.getValue(), access);

    return ResultResponse.of(SuccessResultType.SUCCESS_REISSUE_TOKEN);
  }
}
