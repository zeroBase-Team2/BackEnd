package com.service.sport_companion.api.component.club;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.FixturesEntity;
import com.service.sport_companion.domain.entity.SeasonsEntity;
import com.service.sport_companion.domain.model.dto.response.fixtures.Fixtures;
import com.service.sport_companion.domain.repository.FixturesRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FixtureHandler {

  private final FixturesRepository fixturesRepository;

  /**
   * 크롤링한 경기 일정 저장
   */
  public void saveFixtureList(List<FixturesEntity> fixturesList) {
    fixturesRepository.saveAll(fixturesList);
  }

  /**
   * 전달 받은 날짜와 일치하는 모든 경기 일정 조회
   */
  public List<Fixtures> getAllFixturesList(LocalDate fixtureDate, SeasonsEntity seasons) {
    return mapToFixturesList(fixturesRepository.findAllByFixtureDateAndSeasons(fixtureDate, seasons));
  }

  /**
   * 전달 받은 날짜, 선호구단과 일치하는 경기 일정 조회
   */
  public List<Fixtures> getSupportClubFixturesList(LocalDate fixtureDate, SeasonsEntity seasons, ClubsEntity clubs) {
    return mapToFixturesList(fixturesRepository.findSupportFixtures(fixtureDate, seasons, clubs));
  }

  /**
   * Fixtures DTO 클래스 매핑
   */
  private List<Fixtures> mapToFixturesList(List<FixturesEntity> fixturesEntities) {
    return fixturesEntities.stream()
        .map(fixture -> new Fixtures(
            fixture.getFixtureDate(),
            fixture.getFixtureTime(),
            fixture.getHomeClub().getClubName(),
            fixture.getHomeScore(),
            fixture.getAwayClub().getClubName(),
            fixture.getAwayScore(),
            fixture.getStadium(),
            fixture.getNotes()
        ))
        .toList();
  }
}
