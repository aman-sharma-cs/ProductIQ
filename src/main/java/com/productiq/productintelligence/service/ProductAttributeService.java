package com.productiq.productintelligence.service;

import com.productiq.productintelligence.entity.Product;
import com.productiq.productintelligence.entity.ProductAttribute;
import com.productiq.productintelligence.repository.ProductAttributeRepository;
import com.productiq.productintelligence.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;

    public ProductAttributeService(
            ProductAttributeRepository productAttributeRepository,
            ProductRepository productRepository) {

        this.productAttributeRepository = productAttributeRepository;
        this.productRepository = productRepository;
    }

    public ProductAttribute addAttribute(
            Long productId,
            ProductAttribute attribute) {

        Product product = productRepository.findById(productId)
                .orElseThrow();

        attribute.setProduct(product);

        return productAttributeRepository.save(attribute);
    }
}