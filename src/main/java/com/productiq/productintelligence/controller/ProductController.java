package com.productiq.productintelligence.controller;
import com.productiq.productintelligence.dto.*;
import com.productiq.productintelligence.entity.Product;
import com.productiq.productintelligence.service.AIEnrichmentService;
import com.productiq.productintelligence.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final AIEnrichmentService aiEnrichmentService;

    public ProductController(
            ProductService productService,
            AIEnrichmentService aiEnrichmentService) {

        this.productService = productService;
        this.aiEnrichmentService = aiEnrichmentService;
    }
    @GetMapping("/search")
    public List<ProductSearchResponse> searchProducts(
            @RequestParam String attribute,
            @RequestParam String value) {

        return productService.searchProducts(attribute, value);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/{id}/enrich")
    public Product enrichProduct(@PathVariable Long id) {

        return aiEnrichmentService.enrichProduct(id);
    }
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(
            @PathVariable String category) {

        return productService.getProductsByCategory(category);
    }

    @PostMapping("/natural-search")
    public List<ProductSearchResponse> naturalSearch(
            @RequestBody NaturalLanguageSearchRequest request) {

        NaturalLanguageSearchResponse search =
                aiEnrichmentService
                        .understandSearchQuery(request.getQuery());

        System.out.println("AI Attribute: " + search.getAttribute());
        System.out.println("AI Value: " + search.getValue());

        return productService.searchProducts(
                search.getAttribute(),
                search.getValue()
        );
    }


    @PostMapping("/multi-search-test")
    public NaturalLanguageMultiSearchResponse multiSearchTest(
            @RequestBody NaturalLanguageSearchRequest request) {

        return aiEnrichmentService
                .understandMultiSearchQuery(request.getQuery());
    }

    @GetMapping("/compare/ai/{id1}/{id2}")
    public String compareProductsWithAI(
            @PathVariable Long id1,
            @PathVariable Long id2) {

        ProductComparisonResponse comparison =
                productService.compareProducts(id1, id2);

        return aiEnrichmentService.generateComparisonSummary(comparison);
    }

    @GetMapping("/recommend")
    public List<ProductRecommendationResponse> recommendProducts(
            @RequestParam String attribute,
            @RequestParam String value) {

        return productService.recommendProducts(attribute, value);
    }

    @PostMapping("/recommend/natural")
    public List<ProductRecommendationResponse> recommendByQuery(
            @RequestBody NaturalLanguageSearchRequest request) {

        return productService.recommendProductsByQuery(
                request.getQuery()
        );
    }


}