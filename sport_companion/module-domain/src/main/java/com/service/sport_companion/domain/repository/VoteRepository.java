package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.VoteEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

  boolean existsByStartDate(LocalDate startDate);

  Optional<VoteEntity> findByStartDateBetween(LocalDate voteStartDate, LocalDate voteEndDate);

  Page<VoteEntity> findByStartDateBeforeOrderByStartDateDesc(LocalDate startDate, Pageable pageable);

  @Query("SELECT v "
    + "FROM VoteEntity v JOIN CandidateEntity c ON v = c.voteEntity "
    + "LEFT JOIN UserVoteEntity u ON u.candidateEntity = c "
    + "WHERE v.startDate < :startDate "
    + "GROUP BY v.voteId "
    + "ORDER BY count(*) DESC")
  Page<VoteEntity> findAllOrderByParticipant(LocalDate startDate, Pageable pageable);
}
