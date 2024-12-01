package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.ReservationSiteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSiteRepository extends JpaRepository<ReservationSiteEntity, Long> {

  Optional<ReservationSiteEntity> findBySiteName(String siteName);

}
