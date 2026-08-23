package com.productiq.productintelligence.controller;

import com.productiq.productintelligence.entity.ProductAttribute;
import com.productiq.productintelligence.service.ProductAttributeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @PostMapping("/{productId}/attributes")
    public ProductAttribute addAttribute(
            @PathVariable Long productId,
            @RequestBody ProductAttribute attribute) {

        return productAttributeService.addAttribute(productId, attribute);
    }
}