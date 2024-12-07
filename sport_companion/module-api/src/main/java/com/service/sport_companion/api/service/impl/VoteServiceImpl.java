package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.component.CandidateHandler;
import com.service.sport_companion.api.component.UserVoteHandler;
import com.service.sport_companion.api.component.VoteHandler;
import com.service.sport_companion.api.service.VoteService;
import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.CandidateEntity;
import com.service.sport_companion.domain.entity.VoteEntity;
import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import com.service.sport_companion.domain.model.dto.response.PageResponse;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.vote.CandidateAndCountDto;
import com.service.sport_companion.domain.model.dto.response.vote.CheckVotedResponse;
import com.service.sport_companion.domain.model.dto.response.vote.VoteResponse;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.model.type.SortType;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteServiceImpl implements VoteService {

  private final VoteHandler voteHandler;
  private final CandidateHandler candidateHandler;
  private final UserVoteHandler userVoteHandler;

  @Override
  public ResultResponse<Void> createVote(Long userId, CreateVoteDto voteDto) {
    // 동일한 날짜에 이미 등록돼있으면 추가로 등록할 수 없음
    if (voteHandler.existByStartDate(voteDto.getStartDate())) {
      throw new GlobalException(FailedResultType.ALREADY_EXISTS_VOTE_DATE);
    }

    // 투표 주제 저장
    VoteEntity voteEntity = voteHandler.createVote(voteDto);

    // 투표 후보 5개 저장
    String[] examples = {
      voteDto.getExample1(),
      voteDto.getExample2(),
      voteDto.getExample3(),
      voteDto.getExample4(),
      voteDto.getExample5()
    };

    for (int i = 0; i < examples.length; i++) {
      candidateHandler.createCandidate(CandidateEntity.builder()
          .voteEntity(voteEntity)
          .example(examples[i])
          .sequence(i)
          .build());
    }

    return ResultResponse.of(SuccessResultType.SUCCESS_CREATE_VOTE);
  }

  @Override
  public ResultResponse<Void> updateVote(Long userId, Long voteId, CreateVoteDto voteDto) {
    // 투표 주제 업데이트
    VoteEntity voteEntity = voteHandler.getVoteEntity(voteId);
    voteEntity.update(voteDto);

    // 저장된 투표 후보를 가져와 각각 순서에 맞게 업데이트
    List<CandidateEntity> candidateEntityList = candidateHandler.findByVoteIdOrderBySequence(voteId);

    String[] examples = {
      voteDto.getExample1(),
      voteDto.getExample2(),
      voteDto.getExample3(),
      voteDto.getExample4(),
      voteDto.getExample5()
    };

    for (int i = 0; i < examples.length; i++) {
      candidateEntityList.get(i).updateExample(examples[i]);
    }

    return ResultResponse.of(SuccessResultType.SUCCESS_MODIFY_VOTE);
  }

  @Override
  public ResultResponse<Void> deleteVote(Long userId, Long voteId) {
    candidateHandler.deleteVoteByVoteId(voteId);
    voteHandler.deleteVoteById(voteId);

    return ResultResponse.of(SuccessResultType.SUCCESS_DELETE_VOTE);
  }

  @Override
  public ResultResponse<VoteResponse> getThisWeekVote() {
    // 이번주 투표 시작일인 월요일의 날짜 찾기
    LocalDate voteStartDate = getVoteStartDate(DayOfWeek.MONDAY);

    VoteEntity voteEntity = voteHandler.findByDate(voteStartDate);
    List<CandidateEntity> candidateEntity =
      candidateHandler.findByVoteIdOrderBySequence(voteEntity.getVoteId());

    return new ResultResponse<>(
      SuccessResultType.SUCCESS_GET_VOTE,
      VoteResponse.fromExample(voteEntity, candidateEntity)
    );
  }

  @Override
  public ResultResponse<VoteResponse> getThisWeekVoteResult(Long userId) {
    // 이번주 투표와 결과 조회
    VoteEntity voteEntity = voteHandler.findByDate(getVoteStartDate(DayOfWeek.MONDAY));
    List<CandidateAndCountDto> candidateList =
      candidateHandler.getCandidateByVoteId(voteEntity.getVoteId());

    Long userVotedCandidateId = getUserVotedCandidateId(userId, candidateList);

    return new ResultResponse<>(
      SuccessResultType.SUCCESS_GET_VOTE,
      VoteResponse.fromExampleAndVoteCount(voteEntity, candidateList, userVotedCandidateId)
    );
  }

  @Override
  public ResultResponse<PageResponse<VoteResponse>> getPrevVoteResult(
    Long userId, SortType sortType, Pageable pageable
  ) {
    Page<VoteEntity> voteEntityList = getPrevVoteListBySortType(sortType, pageable);

    // 조회한 투표 별로 투표의 결과와 내가 투표한 후보 id 조회
    List<VoteResponse> response = voteEntityList.stream()
      .map(voteEntity -> {
        List<CandidateAndCountDto> candidateList =
          candidateHandler.getCandidateByVoteId(voteEntity.getVoteId());

        Long votedCandidateId = getUserVotedCandidateId(userId, candidateList);

        return VoteResponse.fromExampleAndVoteCount(voteEntity, candidateList, votedCandidateId);
      })
      .toList();

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_VOTE,
      new PageResponse<>(
        voteEntityList.getNumber(),
        voteEntityList.getTotalPages(),
        voteEntityList.getTotalElements(),
        response
      ));
  }

  @Override
  public ResultResponse<CheckVotedResponse> checkUserVoted(Long userId) {
    return new ResultResponse<>(
      SuccessResultType.SUCCESS_CHECK_VOTED,
      new CheckVotedResponse(userVoteHandler.isVotedToday(userId))
    );
  }

  @Override
  public ResultResponse<VoteResponse> vote(Long userId, Long voteId, Long candidateId) {
    // 오늘 투표한 적 있는지 확인
    if (userVoteHandler.isVotedToday(userId)) {
      throw new GlobalException(FailedResultType.ALREADY_VOTE_TODAY);
    }

    VoteEntity voteEntity = voteHandler.findById(voteId);

    // 현재 기간에 할 수 있는 투표인지 확인
    if (!voteEntity.getStartDate().equals(getVoteStartDate(DayOfWeek.MONDAY))) {
      throw new GlobalException(FailedResultType.CANT_VOTE_PERIOD);
    }
    // 투표할 후보가 해당 투표에 있는지 확인
    if (voteEntity.getCandidateEntity().stream()
      .noneMatch(x -> x.getCandidateId().equals(candidateId))) {
      throw new GlobalException(FailedResultType.CANDIDATE_NOT_FOUND);
    }

    userVoteHandler.vote(userId, candidateId);

    return new ResultResponse<>(
      SuccessResultType.SUCCESS_VOTING,
      VoteResponse.fromExampleAndVoteCount(
        voteEntity, candidateHandler.getCandidateByVoteId(voteId), candidateId)
    );
  }

  // 투표 시작 날짜 구하기
  private LocalDate getVoteStartDate(DayOfWeek dayOfWeek) {
    return LocalDate.now()
      .with(TemporalAdjusters.previousOrSame(dayOfWeek));
  }

  // 사용자가 후보 List 중에서 투표한 곳을 조회
  private Long getUserVotedCandidateId(Long userId, List<CandidateAndCountDto> candidateList) {
    if (userId == null) {
      return null;
    }

    return userVoteHandler.findUserVotedCandidate(
      userId,
      candidateList.stream()
        .mapToLong(x -> x.getCandidateEntity().getCandidateId())
        .boxed()
        .toList()
    );
  }

  // 지난 투표를 원하는 정렬 조건에 맞는 순서로 pageable 크기만큼 조회
  private Page<VoteEntity> getPrevVoteListBySortType(SortType sortType, Pageable pageable) {
    if (sortType.equals(SortType.PARTICIPANT)) {
      return voteHandler.findPrevVoteOrderByParticipant(getVoteStartDate(DayOfWeek.MONDAY), pageable);
    } else {
      return voteHandler.findPrevVoteOrderByUpToDate(getVoteStartDate(DayOfWeek.MONDAY), pageable);
    }
  }
}
