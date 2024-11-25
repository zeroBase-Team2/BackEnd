package com.service.sport_companion.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.ReservationSiteEntity;
import com.service.sport_companion.domain.entity.SportsEntity;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.clubs.Clubs;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import com.service.sport_companion.domain.repository.ClubsRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClubsServiceImplTest {

  @Mock
  private ClubsRepository clubsRepository;

  @InjectMocks
  private ClubsServiceImpl clubsService;

  private List<ClubsEntity> clubsEntities;
  private List<Clubs> clubsList;

  @BeforeEach
  void setUp() {
    // 가짜 ClubsEntity 리스트 생성
    clubsEntities = List.of(
        ClubsEntity.builder()
            .clubId(1L)
            .clubName("KIA 타이거즈")
            .clubStadium("광주 기아 챔피언스 필드")
            .sports(SportsEntity.builder().sportId(1L).build())
            .emblemImg("imageUrl1")
            .introduction(null)
            .reservationSite(ReservationSiteEntity.builder().reservationSiteId(1L).build())
            .build(),
        ClubsEntity.builder()
            .clubId(2L)
            .clubName("삼성 라이온즈")
            .clubStadium("대구 삼성 라이온즈 파크")
            .sports(SportsEntity.builder().sportId(1L).build())
            .emblemImg("imageUrl2")
            .introduction(null)
            .reservationSite(ReservationSiteEntity.builder().reservationSiteId(1L).build())
            .build()
    );

    // 가짜 Clubs 리스트 생성
    clubsList = clubsEntities.stream()
        .map(clubsEntity -> new Clubs(clubsEntity.getClubName(), clubsEntity.getEmblemImg()))
        .toList();
  }

  @Test
  @DisplayName("모든 클럽 리스트 가져오기 성공")
  void getAllClubListSuccessfully() {
    // given
    when(clubsRepository.findAll()).thenReturn(clubsEntities);

    // when
    ResultResponse resultResponse = clubsService.getAllClubList();

    // then
    assertEquals(SuccessResultType.SUCCESS_GET_ALL_CLUBS_LIST.getStatus(), resultResponse.getStatus());
  }
}