package com.service.sport_companion.api.service;

import com.service.sport_companion.domain.model.dto.request.topic.CreateTopicDto;
import com.service.sport_companion.domain.model.dto.response.PageResponse;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import com.service.sport_companion.domain.model.dto.response.topic.CustomTopicResponse;
import com.service.sport_companion.domain.model.dto.response.topic.RecommendCountResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomTopicService {

  ResultResponse<Void> createTopic(Long userId, CreateTopicDto createTopicDto);

  ResultResponse<Void> updateTopic(Long userId, Long topicId, CreateTopicDto updateTopicDto);

  ResultResponse<Void> deleteTopic(Long userId, Long topicId);

  ResultResponse<PageResponse<CustomTopicResponse>> getTopicList(Long userId, Pageable pageable);

  ResultResponse<List<CustomTopicResponse>> getTopicTop5(Long userId);

  ResultResponse<RecommendCountResponse> updateTopicRecommend(Long userId, Long topicId);

  ResultResponse<PageResponse<CustomTopicResponse>> getMyTopicList(Long userId, Pageable pageable);
}
