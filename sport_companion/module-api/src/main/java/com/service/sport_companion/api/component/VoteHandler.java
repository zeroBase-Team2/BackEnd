package com.service.sport_companion.api.component;

import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.VoteEntity;
import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.repository.VoteRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteHandler {

  private final VoteRepository voteRepository;

  public VoteEntity createVote(CreateVoteDto voteDto) {
    return voteRepository.save(VoteEntity.builder()
      .topic(voteDto.getTopic())
      .startDate(voteDto.getStartDate())
      .build());
  }

  public VoteEntity getVoteEntity(Long voteId) {
    return voteRepository.findById(voteId)
      .orElseThrow(() -> new GlobalException(FailedResultType.VOTE_NOT_FOUND));
  }

  public void deleteVoteById(Long voteId) {
    voteRepository.deleteById(voteId);
  }

  public boolean existByStartDate(LocalDate startDate) {
    return voteRepository.existsByStartDate(startDate);
  }
}
