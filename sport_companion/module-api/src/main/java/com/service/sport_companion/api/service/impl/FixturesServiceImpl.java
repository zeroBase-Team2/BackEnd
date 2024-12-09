package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.component.club.FixtureHandler;
import com.service.sport_companion.api.component.club.RestaurantHandler;
import com.service.sport_companion.api.component.club.SupportedClubsHandler;
import com.service.sport_companion.api.component.club.TipsHandler;
import com.service.sport_companion.api.component.crawl.CrawlFixtures;
import com.service.sport_companion.api.service.FixturesService;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.FixturesEntity;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.fixtures.FixtureDetails;
import com.service.sport_companion.domain.model.dto.response.fixtures.Fixtures;
import com.service.sport_companion.domain.model.dto.response.fixtures.Restaurant;
import com.service.sport_companion.domain.model.dto.response.fixtures.Tips;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixturesServiceImpl implements FixturesService {

  private final CrawlFixtures crawlFixtures;
  private final FixtureHandler fixtureHandler;
  private final SupportedClubsHandler supportedClubsHandler;
  private final RestaurantHandler restaurantHandler;
  private final TipsHandler tipsHandler;

  // 경기 일정 크롤링
  @Override
  public ResultResponse<Void> crawlFixtures(String year) {
    crawlFixtures.crawlFixtures(year);

    return ResultResponse.of(SuccessResultType.SUCCESS_CRAWL_FIXTURE);
  }

  // 경기 일정 조회
  @Override
  public ResultResponse<List<Fixtures>> getFixtureList(Long userId, String date) {
    LocalDate fixtureDate = LocalDate.parse(date);

    ClubsEntity clubs = (userId != null) ? supportedClubsHandler.findSupportClubsByUserId(userId) : null;

    // 필요한 데이터를 가져오기
    List<Fixtures> fixturesList = (clubs != null)
        ? fixtureHandler.getSupportClubFixturesList(fixtureDate, clubs)
        : fixtureHandler.getAllFixturesList(fixtureDate);

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_ALL_FIXTURES, fixturesList);
  }

  @Override
  public ResultResponse<FixtureDetails> getFixtureDetails(Long fixtureId) {
    FixturesEntity fixtures = fixtureHandler.findClubByFixtureId(fixtureId);

    List<Restaurant> restaurants = restaurantHandler.findClubRestaurant(fixtures.getHomeClub());

    List<Tips> tips = tipsHandler.findClubTips(fixtures.getHomeClub());

    String siteUrl = fixtures.getFixtureDate().isBefore(LocalDate.now())
        ? fixtures.getHomeClub().getReservationSite().getSiteUrl()
        : null;

    FixtureDetails fixtureDetails = new FixtureDetails(restaurants, tips, siteUrl);

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_FIXTURES_DETAILS, fixtureDetails);
  }
}
