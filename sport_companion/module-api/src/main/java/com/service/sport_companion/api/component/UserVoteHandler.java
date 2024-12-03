package com.service.sport_companion.api.component;

import com.service.sport_companion.domain.repository.UserVoteRepository;
import java.time.LocalDate;
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
}
