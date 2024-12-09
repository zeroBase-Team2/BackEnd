package com.service.sport_companion.domain.model.dto.response.fixtures;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Restaurant {

  private String restaurantName;

  private String restaurantAddress;

  private Double lat;

  private Double lnt;
}
