package com.example.multitenants.config.security.tenant;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class TenantAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public TenantAuthenticationToken(String username, String password) {
        super(username, password);
    }
}