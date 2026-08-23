package com.productiq.productintelligence.service;
import com.productiq.productintelligence.dto.*;
import com.productiq.productintelligence.entity.Product;
import com.productiq.productintelligence.repository.ProductRepository;
import com.productiq.productintelligence.repository.ProductAttributeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.productiq.productintelligence.entity.ProductAttribute;
import com.productiq.productintelligence.dto.ProductRecommendationResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;




import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AIEnrichmentService aiEnrichmentService;
    private final ProductAttributeRepository productAttributeRepository;

    public ProductService(
            ProductRepository productRepository,
            AIEnrichmentService aiEnrichmentService, ProductAttributeRepository productAttributeRepository) {

        this.productRepository = productRepository;
        this.aiEnrichmentService = aiEnrichmentService;
        this.productAttributeRepository = productAttributeRepository;
    }
    public List<ProductSearchResponse> searchProducts(
            String attribute,
            String value) {

        return productAttributeRepository
                .findByAttributeNameIgnoreCaseAndAttributeValueIgnoreCase(
                        attribute,
                        value
                )
                .stream()
                .map(productAttribute -> {

                    Product product = productAttribute.getProduct();

                    return new ProductSearchResponse(
                            product.getId(),
                            product.getName(),
                            product.getCategory(),
                            productAttribute.getAttributeName(),
                            productAttribute.getAttributeValue(),
                            productAttribute.getConfidence()
                    );
                })
                .toList();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


    public Product getProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found with id: " + id
                        )
                );
    }


    public List<Product> getProductsByCategory(String category) {

        return productRepository.findByCategoryIgnoreCase(category);
    }
    public List<ProductSearchResponse> multiAttributeSearch(
            List<NaturalLanguageSearchResponse> filters) {

        List<Product> products = productRepository.findAll();

        for (NaturalLanguageSearchResponse filter : filters) {

            List<ProductAttribute> matchingAttributes =
                    productAttributeRepository
                            .findByAttributeNameIgnoreCaseAndAttributeValueIgnoreCase(
                                    filter.getAttribute(),
                                    filter.getValue()
                            );

            List<Long> matchingProductIds = matchingAttributes.stream()
                    .map(attribute -> attribute.getProduct().getId())
                    .toList();

            // Keep only products matching this filter
            products = products.stream()
                    .filter(product ->
                            matchingProductIds.contains(product.getId()))
                    .toList();
        }

        return products.stream()
                .flatMap(product ->
                        product.getAttributes().stream()
                                .filter(attribute ->
                                        filters.stream().anyMatch(filter ->
                                                attribute.getAttributeName()
                                                        .equalsIgnoreCase(filter.getAttribute())
                                                        &&
                                                        attribute.getAttributeValue()
                                                                .equalsIgnoreCase(filter.getValue())
                                        )
                                )
                                .map(attribute -> new ProductSearchResponse(
                                        product.getId(),
                                        product.getName(),
                                        product.getCategory(),
                                        attribute.getAttributeName(),
                                        attribute.getAttributeValue(),
                                        attribute.getConfidence()
                                ))
                )
                .toList();
    }


    public ProductComparisonResponse compareProducts(Long id1, Long id2) {

        Product product1 = productRepository.findById(id1)
                .orElseThrow();

        Product product2 = productRepository.findById(id2)
                .orElseThrow();

        List<ComparisonItem> comparison = new ArrayList<>();

        for (ProductAttribute attribute1 : product1.getAttributes()) {

            for (ProductAttribute attribute2 : product2.getAttributes()) {

                if (attribute1.getAttributeName()
                        .equalsIgnoreCase(attribute2.getAttributeName())) {

                    comparison.add(
                            new ComparisonItem(
                                    attribute1.getAttributeName(),
                                    attribute1.getAttributeValue(),
                                    attribute2.getAttributeValue()
                            )
                    );
                }
            }
        }

        return new ProductComparisonResponse(
                product1.getName(),
                product2.getName(),
                comparison
        );
    }

    public List<ProductRecommendationResponse> recommendProducts(
            String attribute,
            String value) {

        return productAttributeRepository
                .findByAttributeNameIgnoreCaseAndAttributeValueIgnoreCase(
                        attribute,
                        value
                )
                .stream()
                .map(productAttribute -> {

                    Product product = productAttribute.getProduct();

                    String reason =
                            "Matches " +
                                    productAttribute.getAttributeName() +
                                    " = " +
                                    productAttribute.getAttributeValue();

                    return new ProductRecommendationResponse(
                            product.getId(),
                            product.getName(),
                            reason
                    );
                })
                .toList();
    }


    public List<ProductRecommendationResponse> recommendProductsByQuery(
            String query) {

        NaturalLanguageMultiSearchResponse searchResponse =
                aiEnrichmentService.understandMultiSearchQuery(query);

        List<NaturalLanguageSearchResponse> filters =
                searchResponse.getFilters();

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(product -> {

                    List<ProductAttribute> attributes =
                            product.getAttributes();

                    return filters.stream().allMatch(filter ->
                            attributes.stream().anyMatch(attribute ->
                                    attribute.getAttributeName()
                                            .equalsIgnoreCase(filter.getAttribute())
                                            &&
                                            attribute.getAttributeValue()
                                                    .equalsIgnoreCase(filter.getValue())
                            )
                    );
                })
                .map(product ->
                        new ProductRecommendationResponse(
                                product.getId(),
                                product.getName(),
                                "Matches all requested specifications"
                        )
                )
                .toList();
    }







}