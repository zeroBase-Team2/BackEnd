package com.service.sport_companion.api.controller;

import com.service.sport_companion.api.service.VoteService;
import com.service.sport_companion.domain.model.annotation.CallUser;
import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.dto.response.PageResponse;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.vote.CheckVotedResponse;
import com.service.sport_companion.domain.model.dto.response.vote.VoteResponse;
import com.service.sport_companion.domain.model.type.SortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

  private final VoteService voteService;

  @PostMapping
  public ResponseEntity<ResultResponse<Void>> createVote(@CallUser Long userId,
    @RequestBody CreateVoteDto voteDto
  ) {
    ResultResponse<Void> response = voteService.createVote(userId, voteDto);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @PutMapping("/{voteId}")
  public ResponseEntity<ResultResponse<Void>> updateVote(@CallUser Long userId,
    @PathVariable("voteId") Long voteId, @RequestBody CreateVoteDto voteDto
  ) {
    ResultResponse<Void> response = voteService.updateVote(userId, voteId, voteDto);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @DeleteMapping("/{voteId}")
  public ResponseEntity<ResultResponse<Void>> deleteVote(@CallUser Long userId,
    @PathVariable("voteId") Long voteId
  ) {
    ResultResponse<Void> response = voteService.deleteVote(userId, voteId);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping
  public ResponseEntity<ResultResponse<VoteResponse>> getThisWeekVote() {
    ResultResponse<VoteResponse> response = voteService.getThisWeekVote();

    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/result")
  public ResponseEntity<ResultResponse<VoteResponse>> getThisWeekVoteResult(@CallUser Long userId) {
    ResultResponse<VoteResponse> response = voteService.getThisWeekVoteResult(userId);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/result/prev")
  public ResponseEntity<ResultResponse<PageResponse<VoteResponse>>> getPrevVoteResult(
    @CallUser Long userId,
    @RequestParam(defaultValue = "UP_TO_DATE") SortType sortType,
    Pageable pageable
  ) {
    ResultResponse<PageResponse<VoteResponse>> response = voteService.getPrevVoteResult(userId, sortType, pageable);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/user")
  public ResponseEntity<ResultResponse<CheckVotedResponse>> checkUserVoted(@CallUser Long userId) {
    ResultResponse<CheckVotedResponse> response = voteService.checkUserVoted(userId);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping("{voteId}/candidate/{candidateId}")
  public ResponseEntity<ResultResponse<VoteResponse>> vote(@CallUser Long userId,
    @PathVariable("voteId") Long voteId, @PathVariable("candidateId") Long candidateId
  ) {
    ResultResponse<VoteResponse> response = voteService.vote(userId, voteId, candidateId);

    return new ResponseEntity<>(response, response.getStatus());
  }
}
