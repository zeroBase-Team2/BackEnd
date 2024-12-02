package com.service.sport_companion.domain.model.dto.request.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class GetVoteResult {

  private LocalDate startDate;

  @JsonCreator
  public GetVoteResult(
    @JsonProperty("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate
  ) {
    this.startDate = startDate;
  }
}
