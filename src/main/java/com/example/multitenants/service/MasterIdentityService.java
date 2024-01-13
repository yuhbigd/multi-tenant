package com.example.multitenants.service;

import com.example.multitenants.model.req.CreateTenantUserReq;
import com.example.multitenants.model.res.CreateUserRes;

public interface MasterIdentityService extends IdentityService {
    CreateUserRes createTenantUser(CreateTenantUserReq req) throws Exception;
}
