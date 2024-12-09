package com.service.sport_companion.api.service;

import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.fixtures.FixtureDetails;
import com.service.sport_companion.domain.model.dto.response.fixtures.Fixtures;
import java.util.List;

public interface FixturesService {

  // 경기 일정 크롤링
  ResultResponse<Void> crawlFixtures(String year);

  // 경기 일정 조회
  ResultResponse<List<Fixtures>> getFixtureList(Long userId, String date);

  ResultResponse<FixtureDetails> getFixtureDetails(Long fixtureId);
}
