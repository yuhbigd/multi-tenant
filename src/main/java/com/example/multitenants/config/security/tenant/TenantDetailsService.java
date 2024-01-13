package com.example.multitenants.config.security.tenant;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.model.RoleModel;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.repository.tenant.TenantUserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TenantDetailsService implements UserDetailsService {
    private TenantUserRepository userRepository;

    @Override
    @Transactional("tenantTransactionManager")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optUser = userRepository.findByUsername(username);
        if (!optUser.isPresent()) {
            throw new UsernameNotFoundException("username not found");
        }
        var user = optUser.get();
        System.out.println(user.getRoles());
        return UserModel.builder().username(user.getUsername()).tenant(user.getTenantName())
                .roles(user.getRoles().stream().map(role -> RoleModel.builder().role(role.getRole()).build())
                        .collect(Collectors.toSet()))
                .password(user.getPassword())
                .build();
    }

}