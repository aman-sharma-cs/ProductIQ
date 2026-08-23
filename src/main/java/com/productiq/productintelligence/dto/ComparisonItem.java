package com.productiq.productintelligence.dto;

public class ComparisonItem {

    private String attributeName;
    private String product1Value;
    private String product2Value;

    public ComparisonItem() {
    }

    public ComparisonItem(
            String attributeName,
            String product1Value,
            String product2Value) {

        this.attributeName = attributeName;
        this.product1Value = product1Value;
        this.product2Value = product2Value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getProduct1Value() {
        return product1Value;
    }

    public void setProduct1Value(String product1Value) {
        this.product1Value = product1Value;
    }

    public String getProduct2Value() {
        return product2Value;
    }

    public void setProduct2Value(String product2Value) {
        this.product2Value = product2Value;
    }
}

