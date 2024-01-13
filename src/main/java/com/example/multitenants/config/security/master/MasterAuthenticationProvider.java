package com.example.multitenants.config.security.master;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class MasterAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return MasterAuthenticationToken.class.equals(authentication);
    }
}
