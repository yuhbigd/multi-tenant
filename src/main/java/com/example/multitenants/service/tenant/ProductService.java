package com.example.multitenants.service.tenant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.entity.tenant.Product;
import com.example.multitenants.repository.tenant.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    @Transactional("tenantTransactionManager")
    public void addProduct(Product product) {
        productRepository.save(product);
    }
}
