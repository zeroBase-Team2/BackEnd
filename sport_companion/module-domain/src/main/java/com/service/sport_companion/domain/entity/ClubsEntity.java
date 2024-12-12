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

@Entity(name = "Clubs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ClubsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long clubId;

  private String clubName;

  private String clubStadium;

  private String stadiumAddress;

  private String emblemImg;

  private String reservationSite;
}