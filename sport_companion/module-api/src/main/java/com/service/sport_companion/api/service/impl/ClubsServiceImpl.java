package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.service.ClubsService;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.clubs.Clubs;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import com.service.sport_companion.domain.repository.ClubsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubsServiceImpl implements ClubsService {

  private final ClubsRepository clubsRepository;

  // 모든 구단 조회
  @Override
  public ResultResponse getAllClubList() {
    // 다른 종목 추가시 수정할 예정
    List<Clubs> clubList = clubsRepository.findAll().stream()
        .map(clubsEntity -> new Clubs(clubsEntity.getClubName(), clubsEntity.getEmblemImg()))
        .toList();

    return new ResultResponse(SuccessResultType.SUCCESS_GET_ALL_CLUBS_LIST, clubList);
  }
}
