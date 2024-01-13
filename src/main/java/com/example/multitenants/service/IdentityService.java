package com.example.multitenants.service;

import com.example.multitenants.model.req.CreateUserReq;
import com.example.multitenants.model.req.UserLoginReq;
import com.example.multitenants.model.res.CreateUserRes;
import com.example.multitenants.model.res.UserLoginRes;

public interface IdentityService {
    public UserLoginRes login(UserLoginReq req);

    public CreateUserRes createUser(CreateUserReq req);
}
