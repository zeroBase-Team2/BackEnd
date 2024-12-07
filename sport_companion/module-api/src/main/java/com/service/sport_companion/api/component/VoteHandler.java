package com.service.sport_companion.api.component;

import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.VoteEntity;
import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.repository.VoteRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public VoteEntity findByDate(LocalDate voteStartDate) {
    return voteRepository.findByStartDateBetween(voteStartDate, voteStartDate)
      .orElseThrow(() -> new GlobalException(FailedResultType.CANT_GET_VOTE_RESULT));
  }

  public VoteEntity findById(Long voteId) {
    return voteRepository.findById(voteId)
      .orElseThrow(() -> new GlobalException(FailedResultType.CANT_GET_VOTE_RESULT));
  }

  // 지난 투표 최신순 조회
  public Page<VoteEntity> findPrevVoteOrderByUpToDate(LocalDate thisWeekDate, Pageable pageable) {
    return voteRepository.findByStartDateBeforeOrderByStartDateDesc(thisWeekDate, pageable);
  }

  // 지난 투표 인기순(투표 참여율이 높은순) 조회
  public Page<VoteEntity> findPrevVoteOrderByParticipant(LocalDate thisWeekDate, Pageable pageable) {
    return voteRepository.findAllOrderByParticipant(thisWeekDate, pageable);
  }
}
