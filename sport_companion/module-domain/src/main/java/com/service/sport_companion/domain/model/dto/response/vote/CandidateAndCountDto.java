package com.service.sport_companion.domain.model.dto.response.vote;

import com.service.sport_companion.domain.entity.CandidateEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandidateAndCountDto {

  private CandidateEntity candidateEntity;
  private Long voteCount;
}
