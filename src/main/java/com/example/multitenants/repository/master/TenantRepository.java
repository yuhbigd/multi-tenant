package com.example.multitenants.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.multitenants.entity.master.TenantEntity;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, Long> {

}
