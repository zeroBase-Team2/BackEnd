package com.service.sport_companion.domain.repository;

import com.service.sport_companion.domain.entity.CandidateEntity;
import com.service.sport_companion.domain.model.dto.response.vote.CandidateAndCountDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {

  List<CandidateEntity> findByVoteEntity_VoteIdOrderBySequence(Long voteId);

  @Query("SELECT new com.service.sport_companion.domain.model.dto.response.vote.CandidateAndCountDto(c, COUNT(u)) "
    + "FROM CandidateEntity c "
    + "LEFT JOIN UserVoteEntity u ON c = u.candidateEntity "
    + "WHERE c.voteEntity.voteId = :voteId "
    + "GROUP BY c "
    + "ORDER BY c.sequence")
  List<CandidateAndCountDto> findEntityAndCountByVoteId(@Param("voteId") Long voteId);

  void deleteByVoteEntity_VoteId(Long voteId);
}
