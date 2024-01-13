package com.example.multitenants.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenants.entity.tenant.Product;
import com.example.multitenants.model.req.CreateProductReq;
import com.example.multitenants.model.res.CreateProductRes;
import com.example.multitenants.service.tenant.ProductService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @PostMapping("/product")
    @PreAuthorize("@customSpringAuthorizationRule.isSameTenant(#root)")
    public CreateProductRes addProduct(@RequestBody CreateProductReq req) {
        return productService.addProduct(req);
    }
}
