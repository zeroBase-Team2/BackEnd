package com.service.sport_companion.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Tips")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TipsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tipId;

  @ManyToOne
  @JoinColumn(name = "club_id", nullable = false)
  private ClubsEntity clubs;

  private String SeatName;

  private String theme;

  private String SeatNum;

}
