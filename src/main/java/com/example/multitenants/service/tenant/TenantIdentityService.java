package com.example.multitenants.service.tenant;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.config.security.tenant.TenantAuthenticationToken;
import com.example.multitenants.entity.common.User;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.model.req.CreateUserReq;
import com.example.multitenants.model.req.UserLoginReq;
import com.example.multitenants.model.res.CreateUserRes;
import com.example.multitenants.model.res.UserLoginRes;
import com.example.multitenants.repository.tenant.TenantRoleRepository;
import com.example.multitenants.repository.tenant.TenantUserRepository;
import com.example.multitenants.service.IdentityService;
import com.example.multitenants.util.Constants.USER_ROLE;

import com.example.multitenants.util.CurrentTenant;
import com.example.multitenants.util.IEnum;
import com.example.multitenants.util.JwtUtil;

import lombok.AllArgsConstructor;

@Service(value = "tenantIdentityService")
@AllArgsConstructor
public class TenantIdentityService implements IdentityService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantUserRepository tenantUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserLoginRes login(UserLoginReq req) {
        Authentication authentication = authenticationManager
                .authenticate(new TenantAuthenticationToken(req.getUsername(), req.getPassword()));
        UserModel authenticatedUser = (UserModel) authentication.getPrincipal();
        return UserLoginRes.builder().username(req.getUsername())
                .accessToken(jwtUtil.generateToken(authenticatedUser)).build();
    }

    @Override
    @Transactional(value = "tenantTransactionManager")
    public CreateUserRes createUser(CreateUserReq req) {
        var roleSet = req.getRole().stream().flatMap(role -> {
            var optRole = IEnum.getEnumValueFromValue(role, USER_ROLE.values());
            if (optRole.isPresent()) {
                return Stream.of((USER_ROLE) optRole.get());
            }
            return Stream.empty();
        }).collect(Collectors.toSet());
        var user = User.builder().username(req.getUsername()).password(passwordEncoder.encode(req.getPassword()))
                .roles(tenantRoleRepository.findByRoleIn(roleSet)).tenantName(CurrentTenant.get()).build();
        tenantUserRepository.save(user);
        return CreateUserRes.builder().username(req.getUsername()).build();
    }
}
