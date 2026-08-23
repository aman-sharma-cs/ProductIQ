package com.productiq.productintelligence.dto;

public class ProductSearchResponse {

    private Long id;
    private String name;
    private String category;
    private String matchedAttribute;
    private String matchedValue;
    private Double confidence;

    public ProductSearchResponse() {
    }

    public ProductSearchResponse(
            Long id,
            String name,
            String category,
            String matchedAttribute,
            String matchedValue,
            Double confidence) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.matchedAttribute = matchedAttribute;
        this.matchedValue = matchedValue;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getMatchedAttribute() {
        return matchedAttribute;
    }

    public String getMatchedValue() {
        return matchedValue;
    }

    public Double getConfidence() {
        return confidence;
    }
}