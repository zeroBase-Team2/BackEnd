package com.service.sport_companion.api.service;

import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.dto.response.PageResponse;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.vote.CheckVotedResponse;
import com.service.sport_companion.domain.model.dto.response.vote.VoteResponse;
import com.service.sport_companion.domain.model.type.SortType;
import org.springframework.data.domain.Pageable;

public interface VoteService {

  ResultResponse<Void> createVote(Long userId, CreateVoteDto voteDto);

  ResultResponse<Void> updateVote(Long userId, Long voteId, CreateVoteDto voteDto);

  ResultResponse<Void> deleteVote(Long userId, Long voteId);

  ResultResponse<VoteResponse> getThisWeekVote();

  ResultResponse<VoteResponse> getThisWeekVoteResult(Long userId);

  ResultResponse<PageResponse<VoteResponse>> getPrevVoteResult(Long userId, SortType sortType, Pageable pageable);

  ResultResponse<CheckVotedResponse> checkUserVoted(Long userId);

  ResultResponse<VoteResponse> vote(Long userId, Long voteId, Long candidateId);
}
