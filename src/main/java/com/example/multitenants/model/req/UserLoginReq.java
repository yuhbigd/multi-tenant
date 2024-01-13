package com.example.multitenants.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UserLoginReq {
    private String username;
    private String password;
}
