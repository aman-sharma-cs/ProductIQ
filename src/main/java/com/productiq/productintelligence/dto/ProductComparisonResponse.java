package com.productiq.productintelligence.dto;

import java.util.List;

public class ProductComparisonResponse {

    private String product1;
    private String product2;
    private List<ComparisonItem> comparison;

    public ProductComparisonResponse() {
    }

    public ProductComparisonResponse(
            String product1,
            String product2,
            List<ComparisonItem> comparison) {

        this.product1 = product1;
        this.product2 = product2;
        this.comparison = comparison;
    }

    public String getProduct1() {
        return product1;
    }

    public void setProduct1(String product1) {
        this.product1 = product1;
    }

    public String getProduct2() {
        return product2;
    }

    public void setProduct2(String product2) {
        this.product2 = product2;
    }

    public List<ComparisonItem> getComparison() {
        return comparison;
    }

    public void setComparison(List<ComparisonItem> comparison) {
        this.comparison = comparison;
    }
}

