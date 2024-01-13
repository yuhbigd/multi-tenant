package com.example.multitenants.repository.master;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.multitenants.entity.common.Role;
import com.example.multitenants.util.Constants.USER_ROLE;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleIn(Collection<USER_ROLE> roles);
}
