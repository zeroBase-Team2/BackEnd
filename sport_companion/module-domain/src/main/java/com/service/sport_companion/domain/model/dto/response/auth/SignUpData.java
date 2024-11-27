package com.service.sport_companion.domain.model.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpData {

  private String providerId;
  private String nickname;

}
