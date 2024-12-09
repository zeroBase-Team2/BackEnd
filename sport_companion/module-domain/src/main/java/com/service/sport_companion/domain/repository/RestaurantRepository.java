package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.RestaurantEntity;
import com.service.sport_companion.domain.model.dto.response.fixtures.Restaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  List<RestaurantEntity> findAllByClub(ClubsEntity club);
}
