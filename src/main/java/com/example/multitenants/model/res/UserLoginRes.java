package com.example.multitenants.model.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UserLoginRes {
    private String username;
    @JsonProperty("access_token")
    private String accessToken;
}
