package com.service.sport_companion.domain.entity;

import com.service.sport_companion.domain.model.dto.request.vote.CreateVoteDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vote")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long voteId;

  private LocalDate startDate;

  private String topic;

  @OneToMany(mappedBy = "voteEntity", orphanRemoval = true, cascade = CascadeType.REMOVE)
  private List<CandidateEntity> candidateEntity;

  public void update(CreateVoteDto voteDto) {
    this.startDate = voteDto.getStartDate();
    this.topic = voteDto.getTopic();
  }
}
