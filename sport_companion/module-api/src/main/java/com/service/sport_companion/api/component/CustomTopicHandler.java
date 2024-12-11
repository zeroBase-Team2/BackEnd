package com.service.sport_companion.api.component;

import com.service.sport_companion.core.exception.GlobalException;
import com.service.sport_companion.domain.entity.CustomTopicEntity;
import com.service.sport_companion.domain.entity.UsersEntity;
import com.service.sport_companion.domain.model.dto.request.topic.CreateTopicDto;
import com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto;
import com.service.sport_companion.domain.model.type.FailedResultType;
import com.service.sport_companion.domain.repository.CustomTopicRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomTopicHandler {

  private final CustomTopicRepository customTopicRepository;

  public void saveTopic(UsersEntity userEntity, CreateTopicDto createTopicDto) {
    customTopicRepository.save(CustomTopicEntity.builder()
        .topic(createTopicDto.getTopic())
        .users(userEntity)
      .build());
  }

  public CustomTopicEntity findById(Long topicId) {
    return customTopicRepository.findById(topicId)
      .orElseThrow(() -> new GlobalException(FailedResultType.CUSTOM_TOPIC_NOT_FOUND));
  }

  public void deleteTopic(Long topicId) {
    customTopicRepository.deleteById(topicId);
  }

  public Page<TopicAndRecommendDto> findTopicOrderByCreatedAt(Pageable pageable) {
    return customTopicRepository.findAllByOrderByCreatedAtDesc(pageable);
  }

  public List<TopicAndRecommendDto> findTop5OrderByVoteCount(LocalDateTime createdAt) {
    return customTopicRepository.findTop5ByCreatedAtAfterOrderByVoteCountDesc(createdAt);
  }

  public Page<TopicAndRecommendDto> findTopicByUserId(Long userId, Pageable pageable) {
    return customTopicRepository.findTopicByUserId(userId, pageable);
  }
}
