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

  @ManyToOne
  @JoinColumn(name = "sport_id", nullable = false)
  private SportsEntity sports;

  @ManyToOne
  @JoinColumn(name = "reservation_site_id", nullable = false)
  private ReservationSiteEntity reservationSite;

  private String clubName;

  private String introduction;

  private String clubStadium;

  private String emblemImg;

}