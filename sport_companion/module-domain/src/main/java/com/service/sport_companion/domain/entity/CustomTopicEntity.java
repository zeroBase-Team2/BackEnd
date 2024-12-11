package com.service.sport_companion.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "custom_topic")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CustomTopicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long customTopicId;

  @ManyToOne
  @JoinColumn(name = "users_id", nullable = false)
  private UsersEntity users;

  private String topic;

  @Builder.Default
  private Long voteCount = 0L;

  @CreatedDate
  private LocalDateTime createdAt;

  public void updateTopic(String topic) {
    this.topic = topic;
  }
}
