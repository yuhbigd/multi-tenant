package com.example.multitenants.util;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.stereotype.Component;

import com.example.multitenants.model.UserModel;
import com.example.multitenants.util.Constants.USER_ROLE;

@Component
public class CustomSpringAuthorizationRule {
    public boolean isAdminAndSameTenant(MethodSecurityExpressionOperations operations) {
        UserModel user = (UserModel) operations.getAuthentication().getPrincipal();
        return user.getRoles().stream().anyMatch(role -> role.getRole().equals(USER_ROLE.ADMIN))
                && user.getTenant().equals(CurrentTenant.get());
    }

    public boolean isAdminAndMasterTenant(MethodSecurityExpressionOperations operations) {
        UserModel user = (UserModel) operations.getAuthentication().getPrincipal();
        return user.getRoles().stream().anyMatch(role -> role.getRole().equals(USER_ROLE.ADMIN))
                && user.getTenant().equals("master");
    }

    public boolean isSameTenant(MethodSecurityExpressionOperations operations) {
        UserModel user = (UserModel) operations.getAuthentication().getPrincipal();
        return !CurrentTenant.get().equals("master") && user.getTenant().equals(CurrentTenant.get());
    }
}
