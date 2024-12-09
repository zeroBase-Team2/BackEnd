package com.service.sport_companion.domain.model.dto.response.fixtures;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Fixtures {

  private Long fixtureId;

  private String season;

  private LocalDate fixtureDate;

  private LocalTime fixtureTime;

  private String homeClubName;

  private int homeScore;

  private String awayClubName;

  private int awayScore;

  private String stadium;

  private String stadiumAddress;

  private String notes;
}
