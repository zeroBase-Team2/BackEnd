package com.service.sport_companion.api.component;

import com.service.sport_companion.domain.entity.CandidateEntity;
import com.service.sport_companion.domain.model.dto.response.vote.CandidateAndCountDto;
import com.service.sport_companion.domain.repository.CandidateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandidateHandler {

  private final CandidateRepository candidateRepository;

  public void createCandidate(CandidateEntity candidateEntity) {
    candidateRepository.save(candidateEntity);
  }

  public List<CandidateEntity> findByVoteIdOrderBySequence(Long voteId) {
    return candidateRepository.findByVoteEntity_VoteIdOrderBySequence(voteId);
  }

  public List<CandidateAndCountDto> getCandidateByVoteId(Long voteId) {
    return candidateRepository.findEntityAndCountByVoteId(voteId);
  }

  public void deleteVoteByVoteId(Long voteId) {
    candidateRepository.deleteByVoteEntity_VoteId(voteId);
  }
}
