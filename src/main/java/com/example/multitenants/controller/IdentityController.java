package com.example.multitenants.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenants.model.req.CreateTenantUserReq;
import com.example.multitenants.model.req.CreateUserReq;
import com.example.multitenants.model.req.UserLoginReq;
import com.example.multitenants.model.res.CreateUserRes;
import com.example.multitenants.model.res.UserLoginRes;
import com.example.multitenants.service.IdentityService;
import com.example.multitenants.service.MasterIdentityService;
import com.example.multitenants.util.CurrentTenant;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IdentityController {
    @Qualifier("masterIdentityService")
    private final MasterIdentityService masterIdentityService;
    @Qualifier("tenantIdentityService")
    private final IdentityService tenantIdentityService;

    @PostMapping("/login")
    public UserLoginRes userLogin(@RequestBody UserLoginReq req) {
        if (CurrentTenant.get().equals("master")) {
            return masterIdentityService.login(req);
        }
        return tenantIdentityService.login(req);
    }

    @PreAuthorize("@customSpringAuthorizationRule.isAdminAndSameTenant(#root)")
    @PostMapping("/user")
    public CreateUserRes createUser(@RequestBody CreateUserReq req) {
        if (CurrentTenant.get().equals("master")) {
            return masterIdentityService.createUser(req);
        }
        return tenantIdentityService.createUser(req);
    }

    @PreAuthorize("@customSpringAuthorizationRule.isAdminAndMasterTenant(#root)")
    @PostMapping("/master/user")
    public CreateUserRes createUserInDifferentTenant(@RequestBody CreateTenantUserReq req) throws Exception {
        return masterIdentityService.createTenantUser(req);
    }

}
