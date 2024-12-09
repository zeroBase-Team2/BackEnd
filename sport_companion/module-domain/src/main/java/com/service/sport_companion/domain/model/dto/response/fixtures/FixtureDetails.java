package com.service.sport_companion.domain.model.dto.response.fixtures;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FixtureDetails {

  private List<Restaurant> restaurants;

  private List<Tips> tips;

  private String siteUrl;

}
