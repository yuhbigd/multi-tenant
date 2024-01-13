package com.example.multitenants.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.multitenants.config.security.master.MasterAuthenticationProvider;
import com.example.multitenants.config.security.master.MasterDetailsService;
import com.example.multitenants.config.security.tenant.TenantAuthenticationProvider;
import com.example.multitenants.config.security.tenant.TenantDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final MasterDetailsService masterDetailsService;
    private final TenantDetailsService tenantDetailsService;
    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(tenantAuthenticationProvider(), masterAuthenticationProvider());
    }

    @Bean
    AuthenticationProvider tenantAuthenticationProvider() {
        var authenticationProvider = new TenantAuthenticationProvider();
        authenticationProvider.setUserDetailsService(tenantDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationProvider masterAuthenticationProvider() {
        var authenticationProvider = new MasterAuthenticationProvider();
        authenticationProvider.setUserDetailsService(masterDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        http.authenticationManager(authenticationManager);
        http.httpBasic(basic -> basic.disable()).formLogin(form -> form.disable()).csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(req -> req.requestMatchers("/login").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtSecurityFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
