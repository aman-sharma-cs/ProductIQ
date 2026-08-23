package com.productiq.productintelligence.dto;

import java.util.List;

public class NaturalLanguageMultiSearchResponse {

    private List<NaturalLanguageSearchResponse> filters;

    public NaturalLanguageMultiSearchResponse() {
    }

    public List<NaturalLanguageSearchResponse> getFilters() {
        return filters;
    }

    public void setFilters(List<NaturalLanguageSearchResponse> filters) {
        this.filters = filters;
    }
}