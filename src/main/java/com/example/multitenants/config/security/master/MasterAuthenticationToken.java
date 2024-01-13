package com.example.multitenants.config.security.master;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class MasterAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public MasterAuthenticationToken(String username, String password) {
        super(username, password);
    }
}
