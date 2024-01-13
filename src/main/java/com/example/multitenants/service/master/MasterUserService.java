package com.example.multitenants.service.master;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.entity.common.User;
import com.example.multitenants.repository.tenant.TenantRoleRepository;
import com.example.multitenants.repository.tenant.TenantUserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MasterUserService {
    private TenantUserRepository userRepository;
    private TenantRoleRepository roleRepository;

    @Transactional(value = "tenantTransactionManager")
    public void save() {
        var user = User.builder().username("test").password("1345666433")
                .roles(Set.of(roleRepository.getReferenceById(1l))).tenantName("tenant1").build();
        userRepository.save(user);
    }
}
