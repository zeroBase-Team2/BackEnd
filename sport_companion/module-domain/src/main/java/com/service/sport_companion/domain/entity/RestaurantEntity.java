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

@Entity(name = "Restaurant")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long restaurantId;

  @ManyToOne
  @JoinColumn(name = "club_id", nullable = false)
  private ClubsEntity club;

  private String restaurantName;

  private String restaurantAddress;

  private Double lat;

  private Double lnt;
}
