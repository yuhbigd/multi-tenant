package com.example.multitenants.service.master;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.multitenants.entity.master.TenantEntity;
import com.example.multitenants.repository.master.TenantRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TenantService {
    private TenantRepository tenantRepository;

    public void save() {
        tenantRepository.save(TenantEntity.builder().name(UUID.randomUUID().toString().substring(0, 5))
                .schema(UUID.randomUUID().toString().substring(0, 5)).build());
    }
}
