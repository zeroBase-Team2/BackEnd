package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.ClubsEntity;
import com.service.sport_companion.domain.entity.TipsEntity;
import com.service.sport_companion.domain.model.dto.response.fixtures.Tips;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipsRepository extends JpaRepository<TipsEntity, Long> {

  List<TipsEntity> findAllByClubs(ClubsEntity clubs);
}
