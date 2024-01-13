package com.example.multitenants.service.master;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.config.security.master.MasterAuthenticationToken;
import com.example.multitenants.entity.common.User;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.model.req.CreateTenantUserReq;
import com.example.multitenants.model.req.CreateUserReq;
import com.example.multitenants.model.req.UserLoginReq;
import com.example.multitenants.model.res.CreateUserRes;
import com.example.multitenants.model.res.UserLoginRes;
import com.example.multitenants.repository.master.MasterUserRepository;
import com.example.multitenants.repository.master.RoleRepository;
import com.example.multitenants.repository.tenant.TenantRoleRepository;
import com.example.multitenants.repository.tenant.TenantUserRepository;
import com.example.multitenants.service.IdentityService;
import com.example.multitenants.service.MasterIdentityService;
import com.example.multitenants.util.Constants.USER_ROLE;
import com.example.multitenants.util.CurrentTenant;
import com.example.multitenants.util.ExistedTenants;
import com.example.multitenants.util.IEnum;
import com.example.multitenants.util.JwtUtil;
import com.example.multitenants.util.SelfCallTransaction;

import lombok.RequiredArgsConstructor;

@Service(value = "masterIdentityService")
@RequiredArgsConstructor
public class MasterIdentityServiceImpl implements MasterIdentityService {
    private final MasterUserRepository userRepository;
    private final TenantUserRepository tenantUserRepository;
    private final RoleRepository roleRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SelfCallTransaction selfCallTransaction;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ExistedTenants existedTenants;

    public CreateUserRes createUser(CreateUserReq req) {
        var roleSet = req.getRole().stream().flatMap(role -> {
            var optRole = IEnum.getEnumValueFromValue(role, USER_ROLE.values());
            if (optRole.isPresent()) {
                return Stream.of((USER_ROLE) optRole.get());
            }
            return Stream.empty();
        }).collect(Collectors.toSet());
        var user = User.builder().username(req.getUsername()).password(passwordEncoder.encode(req.getPassword()))
                .roles(roleRepository.findByRoleIn(roleSet)).tenantName("master").build();
        userRepository.save(user);
        return CreateUserRes.builder().username(req.getUsername()).build();
    }

    @Override
    public CreateUserRes createTenantUser(CreateTenantUserReq req) throws Exception {
        String reqTenant = req.getTenant();
        var tenantEntity = existedTenants.findTenant(reqTenant);
        if (tenantEntity.isPresent() && !req.getTenant().equals("master")) {
            CurrentTenant.set(req.getTenant());
            return selfCallTransaction.runInTenantTransaction(() -> createTenantUserHelper(req));
        }
        throw new Exception("Invalid tenant");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private CreateUserRes createTenantUserHelper(CreateTenantUserReq req) {
        var roleSet = req.getRole().stream().flatMap(role -> {
            var optRole = IEnum.getEnumValueFromValue(role, USER_ROLE.values());
            if (optRole.isPresent()) {
                return Stream.of((USER_ROLE) optRole.get());
            }
            return Stream.empty();
        }).collect(Collectors.toSet());
        var user = User.builder().username(req.getUsername()).password(passwordEncoder.encode(req.getPassword()))
                .roles(tenantRoleRepository.findByRoleIn(roleSet)).tenantName(req.getTenant()).build();
        tenantUserRepository.save(user);
        return CreateUserRes.builder().username(req.getUsername()).build();
    }

    @Override
    public UserLoginRes login(UserLoginReq userLoginReq) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new MasterAuthenticationToken(userLoginReq.getUsername(), userLoginReq.getPassword()));
        UserModel authenticatedUser = (UserModel) authentication.getPrincipal();
        return UserLoginRes.builder().username(userLoginReq.getUsername())
                .accessToken(jwtUtil.generateToken(authenticatedUser)).build();
    }
}
