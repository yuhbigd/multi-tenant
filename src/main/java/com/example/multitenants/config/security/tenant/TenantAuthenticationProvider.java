package com.example.multitenants.config.security.tenant;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class TenantAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return TenantAuthenticationToken.class.equals(authentication);
    }
}
