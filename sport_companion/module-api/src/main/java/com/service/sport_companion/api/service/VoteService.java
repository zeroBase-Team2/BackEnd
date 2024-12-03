package com.service.sport_companion.api.service;

import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.dto.request.vote.GetVoteResult;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.vote.CheckVotedResponse;
import com.service.sport_companion.domain.model.dto.response.vote.VoteResponse;

public interface VoteService {

  ResultResponse<Void> createVote(Long userId, CreateVoteDto voteDto);

  ResultResponse<Void> updateVote(Long userId, Long voteId, CreateVoteDto voteDto);

  ResultResponse<Void> deleteVote(Long userId, Long voteId);

  ResultResponse<VoteResponse> getThisWeekVote();

  ResultResponse<VoteResponse> getVoteResult(Long userId, GetVoteResult getVoteResult);

  ResultResponse<CheckVotedResponse> checkUserVoted(Long userId);

  ResultResponse<VoteResponse> vote(Long userId, Long voteId, Long candidateId);
}
