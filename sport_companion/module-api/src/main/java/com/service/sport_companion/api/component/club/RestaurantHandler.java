package com.service.sport_companion.api.component.club;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.model.dto.response.fixtures.Restaurant;
import com.service.sport_companion.domain.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantHandler {

  private final RestaurantRepository restaurantRepository;

  public List<Restaurant> findClubRestaurant(ClubsEntity club) {
    return restaurantRepository.findAllByClub(club).stream()
        .map(restaurantEntity -> new Restaurant(
            restaurantEntity.getRestaurantName(),
            restaurantEntity.getRestaurantAddress(),
            restaurantEntity.getLat(),
            restaurantEntity.getLnt()
        )).toList();
  }
}
