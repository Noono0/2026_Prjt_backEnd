package com.noonoo.prjtbackend.product.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.product.dto.ProductDto;
import com.noonoo.prjtbackend.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<List<ProductDto>> findProducts() {
        return ApiResponse.ok(productService.findProducts());
    }
}
