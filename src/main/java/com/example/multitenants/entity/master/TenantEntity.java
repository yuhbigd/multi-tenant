package com.example.multitenants.entity.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TENANTS")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TENANT_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "TENANT_ID_SEQUENCE_GENERATOR", sequenceName = "TENANT_ID_SEQUENCE", allocationSize = 1)
    @Column(name = "TENANT_ID")
    private Long id;
    @Column(name = "TENANT_NAME")
    private String name;
    @Column(name = "TENANT_SCHEMA")
    private String schema;
}
