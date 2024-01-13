package com.example.multitenants.config.security.master;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.multitenants.model.RoleModel;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.repository.master.MasterUserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MasterDetailsService implements UserDetailsService {
    private MasterUserRepository userRepository;

    // thats goood:
    // https://github.com/apache/kylin/blob/kylin5/src/common-service/src/main/java/org/apache/kylin/rest/security/LimitLoginAuthenticationProvider.java
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optUser = userRepository.findByUsername(username);
        if (!optUser.isPresent()) {
            throw new UsernameNotFoundException("username not found");
        }
        var user = optUser.get();
        return UserModel.builder().username(user.getUsername()).tenant(user.getTenantName())
                .roles(user.getRoles().stream().map(role -> RoleModel.builder().role(role.getRole()).build())
                        .collect(Collectors.toSet()))
                .password(user.getPassword())
                .build();
    }

}