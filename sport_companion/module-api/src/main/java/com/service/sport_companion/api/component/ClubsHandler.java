package com.service.sport_companion.api.component;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.model.dto.response.clubs.Clubs;
import com.service.sport_companion.domain.repository.ClubsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubsHandler {

  private final ClubsRepository clubsRepository;

  public ClubsEntity findByClubName(String clubName) {
    return clubsRepository.findByClubName(clubName);
  }

  public List<Clubs> getAllClubList() {
    return clubsRepository.findAll().stream()
        .map(clubsEntity -> new Clubs(clubsEntity.getClubId(), clubsEntity.getClubName()))
        .toList();
  }
}
