package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.CustomTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTopicRepository extends JpaRepository<CustomTopicEntity, Long> {

}