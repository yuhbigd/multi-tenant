package com.example.multitenants.repository.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.multitenants.entity.common.Role;
import com.example.multitenants.util.Constants.USER_ROLE;

import java.util.Collection;
import java.util.Set;

@Repository
public interface TenantRoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleIn(Collection<USER_ROLE> roles);
}
