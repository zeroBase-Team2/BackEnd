package com.service.sport_companion.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.service.sport_companion.api.component.UserHandler;
import com.service.sport_companion.api.component.club.ClubsFacade;
import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.UsersEntity;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.user.UserInfo;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import com.service.sport_companion.domain.model.type.UserRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

  @Mock
  private UserHandler userHandler;

  @Mock
  private ClubsFacade clubsFacade;

  @InjectMocks
  private UsersServiceImpl usersService;

  private static final Long USER_ID = 1L;
  private static final String NICKNAME = "nickname";
  private static final String NEW_NICKNAME = "new nickname";

  private UsersEntity users;
  private ClubsEntity clubs;
  private UserInfo userInfo;


  @BeforeEach
  void setUp() {

    users = UsersEntity.builder()
        .userId(USER_ID)
        .email("user@example.com")
        .nickname(NICKNAME)
        .provider("kakao")
        .providerId("1234")
        .role(UserRole.USER)
        .createdAt(LocalDateTime.now())
        .build();


    clubs = ClubsEntity.builder()
        .clubId(1L)
        .clubName("KIA 타이거즈")
        .reservationSite("url~~")
        .build();

    userInfo = new UserInfo(
        users.getEmail(),
        users.getNickname(),
        clubs.getClubName(),
        clubs.getEmblemImg()
    );

  }


  @Test
  @DisplayName("사용자 정보 조회 성공")
  void UserInfoSuccess() {
    //given
    when(userHandler.findByUserId(USER_ID)).thenReturn(users);
    when(clubsFacade.getSupportClubsByUserId(USER_ID)).thenReturn(clubs);

    // when
    ResultResponse<UserInfo> response = usersService.getUserInfo(USER_ID);

    // then
    assertEquals(SuccessResultType.SUCCESS_GET_USERINFO.getStatus(), response.getStatus());
    assertEquals(userInfo.getEmail(), response.getData().getEmail());
    assertEquals(userInfo.getNickname(), response.getData().getNickname());
    assertEquals(userInfo.getClubName(), response.getData().getClubName());
    assertEquals(userInfo.getEmblemImg(), response.getData().getEmblemImg());
  }

  @Test
  @DisplayName("사용자 정보 조회 실패 : 존재하지 않은 사용자")
  void UserInfoFail_UserNotFound() {
    //given
    when(userHandler.findByUserId(USER_ID))
        .thenThrow(new GlobalException(FailedResultType.USER_NOT_FOUND));

    // when & then
    assertThrows(GlobalException.class, () -> usersService.getUserInfo(USER_ID));
  }

  @Test
  @DisplayName("사용자 정보 수정 성공")
  void updateUserInfoSuccess() {
    // given
    when(userHandler.findByUserId(USER_ID)).thenReturn(users);

    // when
    ResultResponse<Void> response = usersService.updateUserInfo(USER_ID, NEW_NICKNAME);

    // then
    assertEquals(SuccessResultType.SUCCESS_UPDATE_USERINFO.getStatus(), response.getStatus());
  }

  @Test
  @DisplayName("사용자 정보 수정 실패 : 존재하지 않은 사용자")
  void updateUserInfoFail_UserNotFound() {
    // given
    when(userHandler.findByUserId(USER_ID))
        .thenThrow(new GlobalException(FailedResultType.USER_NOT_FOUND));

    // when & then
    assertThrows(GlobalException.class, () -> usersService.getUserInfo(USER_ID));
  }
}