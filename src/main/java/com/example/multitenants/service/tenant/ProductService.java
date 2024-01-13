package com.example.multitenants.service.tenant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.multitenants.entity.tenant.Product;
import com.example.multitenants.model.req.CreateProductReq;
import com.example.multitenants.model.res.CreateProductRes;
import com.example.multitenants.repository.tenant.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    @Transactional("tenantTransactionManager")
    public CreateProductRes addProduct(CreateProductReq req) {
        productRepository.save(Product.builder().name(req.getName()).build());
        return CreateProductRes.builder().name(req.getName()).build();
    }
}
