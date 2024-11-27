package com.service.sport_companion.api.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.ReservationSiteEntity;
import com.service.sport_companion.domain.entity.SportsEntity;
import com.service.sport_companion.domain.model.dto.response.clubs.Clubs;
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
class ClubsHandlerTest {

  @Mock
  private ClubsRepository clubsRepository;

  @InjectMocks
  private ClubsHandler clubsHandler;

  private final String CLUB_NAME = "KIA 타이거즈";

  private ClubsEntity club;
  private List<ClubsEntity> clubsEntities;
  private List<Clubs> clubsList;

  @BeforeEach
  void setUp() {
    club = ClubsEntity.builder()
        .clubId(1L)
        .clubName(CLUB_NAME)
        .clubStadium("광주 기아 챔피언스 필드")
        .sports(SportsEntity.builder().sportId(1L).build())
        .emblemImg("imageUrl1")
        .introduction(null)
        .reservationSite(ReservationSiteEntity.builder().reservationSiteId(1L).build())
        .build();

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

    clubsList = clubsEntities.stream()
        .map(clubsEntity -> new Clubs(clubsEntity.getClubId(), clubsEntity.getClubName()))
        .toList();
  }

  @Test
  @DisplayName("findByClubName : 구단 정보 반환 성공")
  void shouldReturnClubs() {
    // given
    when(clubsRepository.findByClubName(CLUB_NAME)).thenReturn(club);

    // when
    ClubsEntity response = clubsHandler.findByClubName(CLUB_NAME);

    // then
    assertEquals(response, club);
  }

  @Test
  @DisplayName("getAllClubList : 모든 구단 정보 반환 성공")
  void shouldReturnClubList() {
    // given
    when(clubsRepository.findAll()).thenReturn(clubsEntities);

    // when
    List<Clubs> response = clubsHandler.getAllClubList();

    // then
    assertEquals(clubsList.size(), response.size());
  }
}