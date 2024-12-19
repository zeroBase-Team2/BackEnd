package com.service.sport_companion.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.service.sport_companion.api.component.club.ClubsFacade;
import com.service.sport_companion.api.component.crawl.CrawlFixtures;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.FixturesEntity;
import com.service.sport_companion.domain.entity.RestaurantEntity;
import com.service.sport_companion.domain.entity.TipsEntity;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.fixtures.FixtureDetails;
import com.service.sport_companion.domain.model.dto.response.fixtures.Fixtures;
import com.service.sport_companion.domain.model.dto.response.fixtures.Restaurant;
import com.service.sport_companion.domain.model.dto.response.fixtures.Tips;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FixturesServiceImplTest {

  @Mock
  private CrawlFixtures crawlFixtures;

  @Mock
  private ClubsFacade clubsFacade;


  @InjectMocks
  private FixturesServiceImpl fixturesService;

  private final String YEAR = "2024";
  private final String MONTH = "12";
  private final String DAY = "05";
  private final String DATE = "2024-12-05";
  private final String SEASON_NAME = "정규 시즌";
  private final Long USERID = 1L;

  private LocalDate localDate = null;
  private List<Fixtures> fixtures;
  private ClubsEntity homeClub;
  private ClubsEntity awayClub;
  private List<RestaurantEntity> restaurant;
  private List<TipsEntity> tip;
  private FixtureDetails fixtureDetails;


  @BeforeEach
  void setUp() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = YEAR + "-" + MONTH + "-" + DAY;
    localDate = LocalDate.parse(formattedDate, formatter);


    homeClub = ClubsEntity.builder()
        .clubId(1L)
        .clubName("KIA 타이거즈")
        .reservationSite("url~~")
        .build();

    awayClub = ClubsEntity.builder()
        .clubId(1L)
        .clubName("삼성 라이온즈")
        .reservationSite("url~~")
        .build();

    List<FixturesEntity> fixturesList = List.of(
        FixturesEntity.builder()
            .fixtureId(4233L)
            .fixtureDate(LocalDate.parse("2024-12-05"))
            .fixtureTime(LocalTime.parse("18:30"))
            .homeScore(5)
            .awayScore(0)
            .notes("-")
            .homeClub(homeClub)
            .awayClub(awayClub)
            .season("정규 시즌")
            .build()
    );

    fixtures = fixturesList.stream()
        .map(fixture -> new Fixtures(
            fixture.getFixtureId(),
            fixture.getSeason(),
            fixture.getFixtureDate(),
            fixture.getFixtureTime(),
            fixture.getHomeClub().getClubName(),
            fixture.getHomeScore(),
            fixture.getAwayClub().getClubName(),
            fixture.getAwayScore(),
            fixture.getHomeClub().getClubStadium(),
            fixture.getHomeClub().getStadiumAddress(),
            fixture.getNotes()
        ))
        .toList();

    restaurant = List.of(
        RestaurantEntity.builder()
            .club(homeClub)
            .restaurantId(1L)
            .restaurantName("Restaurant A")
            .restaurantAddress("123 Main St")
            .lat(37.7749)
            .lnt(-122.4194)
            .build(),
        RestaurantEntity.builder()
            .club(homeClub)
            .restaurantId(2L)
            .restaurantName("Restaurant B")
            .restaurantAddress("456 Elm St")
            .lat(40.7128)
            .lnt(-74.0060)
            .build()
    );

    tip = List.of(
        TipsEntity.builder()
            .tipId(1L)
            .clubs(homeClub)
            .SeatName("A1")
            .theme("VIP")
            .SeatNum("101")
            .build(),
        TipsEntity.builder()
            .tipId(2L)
            .clubs(homeClub)
            .SeatName("B2")
            .theme("Standard")
            .SeatNum("202")
            .build()
    );


    fixtureDetails = new FixtureDetails(
        Restaurant.of(restaurant),
        Tips.of(tip),
        homeClub.getReservationSite()
    );
  }

  @Test
  @DisplayName("년도 별 경기 일정 크롤링 성공")
  void crawlFixturesSuccess() {
    // given
    doNothing().when(crawlFixtures).crawlFixtures(YEAR);

    // when
    ResultResponse<Void> resultResponse = fixturesService.crawlFixtures(YEAR);

    // then
    assertEquals(SuccessResultType.SUCCESS_CRAWL_FIXTURE.getStatus(), resultResponse.getStatus());
  }

  @Test
  @DisplayName("선호구단 경기 일정 조회 성공")
  void getSupportClubFixturesSuccess() {
    // given
    when(clubsFacade.getFixtureList(localDate, USERID)).thenReturn(fixtures);

    // when
    ResultResponse<List<Fixtures>> resultResponse = fixturesService.getFixtureList(USERID, DATE);

    // then
    assertEquals(SuccessResultType.SUCCESS_GET_ALL_FIXTURES.getStatus(), resultResponse.getStatus());
    assertEquals(fixtures.size(), resultResponse.getData().size());
    assertEquals(fixtures.getFirst().getHomeClubName(), resultResponse.getData().getFirst().getHomeClubName());
  }

  @Test
  @DisplayName("특정 구단 경기 일정 조회 성공")
  void getClubFixturesSuccess() {
    // given
    when(clubsFacade.getClubFixtureList(homeClub.getClubName(), localDate)).thenReturn(fixtures);

    // when
    ResultResponse<List<Fixtures>> resultResponse = fixturesService.getClubFixtureList(homeClub.getClubName(), DATE);

    // then
    assertEquals(SuccessResultType.SUCCESS_GET_CLUB_FIXTURES.getStatus(), resultResponse.getStatus());
    assertEquals(fixtures.size(), resultResponse.getData().size());
    assertEquals(fixtures.getFirst().getHomeClubName(), resultResponse.getData().getFirst().getHomeClubName());
  }


  @Test
  @DisplayName("경기 상세 정보 조회")
  void getAllClubFixturesSuccess() {
    // given
    when(clubsFacade.getFixtureDetails(homeClub.getClubId())).thenReturn(fixtureDetails);

    // when
    ResultResponse<FixtureDetails> resultResponse = fixturesService.getFixtureDetails(homeClub.getClubId());

    // then
    assertEquals(SuccessResultType.SUCCESS_GET_FIXTURES_DETAILS.getStatus(), resultResponse.getStatus());
  }

}