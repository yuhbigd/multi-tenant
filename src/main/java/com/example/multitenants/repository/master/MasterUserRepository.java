package com.example.multitenants.repository.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.multitenants.entity.common.User;

@Repository
public interface MasterUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
