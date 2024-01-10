package com.example.multitenants.repository.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.multitenants.entity.tenant.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}