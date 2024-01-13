package com.example.multitenants.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateUserReq {
    private String username;
    private String password;
    private Set<String> role;
}
