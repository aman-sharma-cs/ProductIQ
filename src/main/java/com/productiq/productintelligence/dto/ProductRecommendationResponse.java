package com.productiq.productintelligence.dto;

public class ProductRecommendationResponse {

    private Long productId;
    private String productName;
    private String reason;

    public ProductRecommendationResponse() {
    }

    public ProductRecommendationResponse(
            Long productId,
            String productName,
            String reason) {

        this.productId = productId;
        this.productName = productName;
        this.reason = reason;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getReason() {
        return reason;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

