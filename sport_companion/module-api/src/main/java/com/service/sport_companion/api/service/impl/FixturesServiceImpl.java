package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.component.FixtureHandler;
import com.service.sport_companion.api.component.SeasonHandler;
import com.service.sport_companion.api.component.SupportedClubsHandler;
import com.service.sport_companion.api.component.crawl.CrawlFixtures;
import com.service.sport_companion.api.service.FixturesService;
import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.FixturesEntity;
import com.service.sport_companion.domain.entity.SeasonsEntity;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.fixtures.Fixtures;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
  private final SeasonHandler seasonHandler;
  private final SupportedClubsHandler supportedClubsHandler;

  @Override
  public ResultResponse<Void> crawlFixtures(String year) {
    crawlFixtures.crawlFixtures(year);

    return ResultResponse.of(SuccessResultType.SUCCESS_CRAWL_FIXTURE);
  }

  @Override
  public ResultResponse<List<Fixtures>> getFixtureList(Long userId, String year, String month, String day, String seasonName) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = year + "-" + month + "-" + day;
    LocalDate fixtureDate = LocalDate.parse(formattedDate, formatter);

    SeasonsEntity seasons = seasonHandler.findBySeasonName(seasonName);

    ClubsEntity clubs = (userId != null) ? supportedClubsHandler.findSupportClubsByUserId(userId) : null;

    // 필요한 데이터를 가져오기
    List<Fixtures> fixturesList = (clubs != null)
        ? fixtureHandler.getSupportClubFixturesList(fixtureDate, seasons, clubs)
        : fixtureHandler.getAllFixturesList(fixtureDate, seasons);

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_ALL_FIXTURES, fixturesList);
  }
}
