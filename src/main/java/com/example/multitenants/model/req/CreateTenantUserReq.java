package com.example.multitenants.model.req;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateTenantUserReq {
    private String username;
    private String password;
    private String tenant;
    private Set<String> role;
}
