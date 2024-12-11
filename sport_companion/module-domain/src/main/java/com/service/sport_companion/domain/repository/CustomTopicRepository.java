package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.CustomTopicEntity;
import com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTopicRepository extends JpaRepository<CustomTopicEntity, Long> {

  @Query("SELECT new com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto(c, COUNT(r)) "
    + "FROM CustomTopicEntity c "
    + "LEFT JOIN CustomTopicRecommendEntity r ON c = r.customTopic "
    + "GROUP BY c "
    + "ORDER BY c.createdAt DESC ")
  Page<TopicAndRecommendDto> findAllByOrderByCreatedAtDesc(Pageable pageable);

  @Query("SELECT new com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto(c, COUNT(r)) "
    + "FROM CustomTopicEntity c "
    + "LEFT JOIN CustomTopicRecommendEntity r ON c = r.customTopic "
    + "WHERE c.createdAt > :createdAt "
    + "GROUP BY c "
    + "ORDER BY COUNT(r) DESC "
    + "LIMIT 5")
  List<TopicAndRecommendDto> findTop5ByCreatedAtAfterOrderByVoteCountDesc(
    @Param("createdAt") LocalDateTime createdAt);

  @Query("SELECT new com.service.sport_companion.domain.model.dto.response.topic.TopicAndRecommendDto(c, COUNT(r)) "
    + "FROM CustomTopicEntity c "
    + "LEFT JOIN CustomTopicRecommendEntity r ON c = r.customTopic "
    + "WHERE c.users.userId = :userId "
    + "GROUP BY c "
    + "ORDER BY c.createdAt DESC ")
  Page<TopicAndRecommendDto> findTopicByUserId(@Param("userId") Long userId, Pageable pageable);
}
