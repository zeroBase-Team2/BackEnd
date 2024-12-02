package com.service.sport_companion.api.service.impl;

import com.service.sport_companion.api.component.CustomTopicHandler;
import com.service.sport_companion.api.component.CustomTopicRecommendHandler;
import com.service.sport_companion.api.component.UserHandler;
import com.service.sport_companion.api.service.CustomTopicService;
import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.CustomTopicEntity;
import com.service.sport_companion.domain.entity.UsersEntity;
import com.service.sport_companion.domain.model.dto.request.topic.CreateTopicDto;
import com.service.sport_companion.domain.model.dto.response.PageResponse;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.topic.CustomTopicResponse;
import com.service.sport_companion.domain.model.dto.response.topic.RecommendCountResponse;
import com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.model.type.SuccessResultType;
import com.service.sport_companion.domain.model.type.UserRole;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomTopicServiceImpl implements CustomTopicService {

  private final CustomTopicHandler customTopicHandler;
  private final UserHandler userHandler;
  private final CustomTopicRecommendHandler customTopicRecommendHandler;

  @Override
  public ResultResponse<Void> createTopic(Long userId, CreateTopicDto createTopicDto) {
    UsersEntity userEntity = userHandler.findByUserId(userId);
    customTopicHandler.saveTopic(userEntity, createTopicDto);

    return ResultResponse.of(SuccessResultType.SUCCESS_CREATE_CUSTOM_TOPIC);
  }

  @Override
  public ResultResponse<Void> deleteTopic(Long userId, Long topicId) {
    UsersEntity userEntity = userHandler.findByUserId(userId);
    CustomTopicEntity customTopicEntity = customTopicHandler.findById(topicId);

    // 작성자이거나 관리자인 경우 삭제할 수 있다.
    if (!customTopicEntity.getUsers().equals(userEntity)
      && !userEntity.getRole().equals(UserRole.ADMIN)) {
      throw new GlobalException(FailedResultType.DELETE_TOPIC_FORBIDDEN);
    }

    customTopicHandler.deleteTopic(topicId);

    return ResultResponse.of(SuccessResultType.SUCCESS_DELETE_CUSTOM_TOPIC);
  }

  @Override
  public ResultResponse<PageResponse<CustomTopicResponse>> getTopicList(Long userId,
    Pageable pageable
  ) {
    // 페이지 요청값이 없으면 전체를 조회
    if (pageable == null) {
      pageable = Pageable.unpaged();
    }

    Page<TopicAndRecommendDto> topicPage = customTopicHandler.findTopicOrderByCreatedAt(pageable);

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_CUSTOM_TOPIC,
      new PageResponse<>(
        topicPage.getNumber(),
        topicPage.getTotalPages(),
        topicPage.getTotalElements(),
        topicPage.getContent().stream()
          .map(topic -> CustomTopicResponse.of(
            topic.getCustomTopicEntity(),
            isAuthor(userId, topic.getCustomTopicEntity().getUsers().getUserId()),
            topic.getRecommendCount())
          )
          .toList()));
  }

  @Override
  public ResultResponse<List<CustomTopicResponse>> getTopicTop5(Long userId) {
    // 일주일 간 올라온 토픽 중 top5를 선정하기 위해 7일 전 날짜 저장
    LocalDateTime before7Days = LocalDateTime.now().minusDays(7);

    List<TopicAndRecommendDto> topicPage = customTopicHandler.findTop5OrderByVoteCount(before7Days);

    return new ResultResponse<>(SuccessResultType.SUCCESS_GET_CUSTOM_TOPIC, topicPage.stream()
      .map(topic -> CustomTopicResponse.of(
        topic.getCustomTopicEntity(),
        isAuthor(userId, topic.getCustomTopicEntity().getUsers().getUserId()),
        topic.getRecommendCount())
      )
      .toList());
  }

  @Override
  public ResultResponse<RecommendCountResponse> updateTopicRecommend(Long userId, Long topicId) {
    // 이미 추천한적 있다면 추천할 수 없음
    if (customTopicRecommendHandler.existsTopicRecommend(userId, topicId)) {
      throw new GlobalException(FailedResultType.ALREADY_RECOMMEND_TOPIC);
    }
    // 사용자 추천 내역을 DB에 추가
    customTopicRecommendHandler.saveByUserIdAndTopicId(userId, topicId);

    // 현재 추천수 반환
    return new ResultResponse<>(SuccessResultType.SUCCESS_RECOMMEND_TOPIC,
      new RecommendCountResponse(customTopicRecommendHandler.getRecommendCount(topicId)));
  }

  private boolean isAuthor(Long userId, Long authorId) {
    return authorId.equals(userId);
  }
}
