package com.productiq.productintelligence.dto;

public class NaturalLanguageSearchResponse {

    private String attribute;
    private String value;

    public NaturalLanguageSearchResponse() {
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}