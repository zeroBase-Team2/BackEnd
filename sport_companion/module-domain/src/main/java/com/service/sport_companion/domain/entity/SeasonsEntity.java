package com.service.sport_companion.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Seasons")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SeasonsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seasonId;

  private String seasonName;

}
