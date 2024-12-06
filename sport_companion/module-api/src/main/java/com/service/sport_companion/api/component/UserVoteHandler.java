package com.service.sport_companion.api.component;

import com.service.sport_companion.domain.repository.UserVoteRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVoteHandler {

  private final UserVoteRepository userVoteRepository;

  public boolean isVotedToday(Long userId) {
    return userVoteRepository.findTodayVote(userId, LocalDate.now()).isPresent();
  }

  public void vote(Long userId, Long candidateId) {
    userVoteRepository.vote(userId, candidateId);
  }

  public Long findUserVotedCandidate(Long userId, List<Long> candidateList) {
    return userVoteRepository.findMyRecentVoted(userId, candidateList)
      .map(voteEntity -> voteEntity.getCandidateEntity().getCandidateId())
      .orElse(null);
  }
}
