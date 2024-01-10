package com.example.multitenants.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.multitenants.entity.master.TenantEntity;
import com.example.multitenants.repository.master.TenantRepository;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Data
public class ExistedTenants {
    private final ConcurrentHashMap<String, TenantEntity> map = new ConcurrentHashMap<>();
    private final TenantRepository tenantRepository;

    @PostConstruct
    private void init() {
        var tenants = tenantRepository.findAll();
        tenants.forEach(tenant -> {
            map.put(tenant.getName(), tenant);
        });
    }

    public void clear() {
        map.clear();
    }

    public void reset() {
        init();
    }

    public Optional<TenantEntity> findTenant(String tenantName) {
        return Optional.ofNullable(map.get(tenantName));
    }

    public void update(String tenantName, TenantEntity tenant) {
        if (findTenant(tenantName).isPresent()) {
            map.put(tenantName, tenant);
        }
    }

    public void deleteTenant(String tenantName) {
        map.remove(tenantName);
    }
}
