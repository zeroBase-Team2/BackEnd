package com.service.sport_companion.api.controller;

import com.service.sport_companion.api.service.ClubsService;
import com.service.sport_companion.domain.model.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class ClubsController {

  private final ClubsService clubsService;

  @GetMapping("/all")
  public ResponseEntity<ResultResponse> getAllClubs() {

    ResultResponse response = clubsService.getAllClubList();
    return new ResponseEntity<>(response, response.getStatus());
  }

}
