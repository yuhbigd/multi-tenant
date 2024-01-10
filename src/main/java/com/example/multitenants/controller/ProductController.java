package com.example.multitenants.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.multitenants.entity.tenant.Product;
import com.example.multitenants.service.tenant.ProductService;

import lombok.AllArgsConstructor;
import com.example.multitenants.service.master.TenantService;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private TenantService tenantService;

    @PostMapping("/product")
    public String addProduct() {
        tenantService.save();
        productService.addProduct(Product.builder().name(UUID.randomUUID().toString().substring(0, 5)).build());
        return "entity";
    }

}
