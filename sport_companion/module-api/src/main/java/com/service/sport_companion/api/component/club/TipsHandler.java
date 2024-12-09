package com.service.sport_companion.api.component.club;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.model.dto.response.fixtures.Tips;
import com.service.sport_companion.domain.repository.TipsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipsHandler {

  private final TipsRepository tipsRepository;

  public List<Tips> findClubTips(ClubsEntity clubs) {
    return tipsRepository.findAllByClubs(clubs).stream()
        .map(tips -> new Tips(
            tips.getSeatName(),
            tips.getTheme(),
            tips.getSeatNum()
        )).toList();
  }

}
