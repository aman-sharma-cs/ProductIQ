package com.productiq.productintelligence.service;
import com.productiq.productintelligence.dto.ProductComparisonResponse;
import com.productiq.productintelligence.entity.ProductAttribute;
import com.productiq.productintelligence.repository.ProductAttributeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.productiq.productintelligence.dto.AIAttributeResponse;
import com.productiq.productintelligence.entity.Product;
import com.productiq.productintelligence.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.productiq.productintelligence.dto.NaturalLanguageSearchResponse;
import com.productiq.productintelligence.dto.NaturalLanguageMultiSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AIEnrichmentService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final ProductAttributeRepository productAttributeRepository;

    public AIEnrichmentService(
            ProductRepository productRepository,
            ProductAttributeRepository productAttributeRepository) {

        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Product enrichProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found with id: " + productId
                        )
                );

        System.out.println("Product name: " + product.getName());
        System.out.println("Product description: " + product.getDescription());

        try (Client client = new Client()) {

            String prompt = """
                    You are an industrial product data extraction system.

                    Analyze the following product information.

                    Product name:
                    %s

                    Product description:
                    %s

                    Extract ONLY product specifications that are explicitly supported
                    by the provided information.

                    Do NOT extract the product name as an attribute.

                    Do NOT invent, assume, or guess specifications.

                    For each specification provide:
                    - attributeName
                    - attributeValue
                    - confidence
                    - evidence

                    Return the extracted attributes as JSON.
                    """.formatted(
                    product.getName(),
                    product.getDescription()
            );

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.1-flash-lite",
                            prompt,
                            null
                    );

            System.out.println("Gemini response:");
            System.out.println(response.text());

            String json = response.text()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            List<AIAttributeResponse> attributes =
                    objectMapper.readValue(
                            json,
                            new TypeReference<List<AIAttributeResponse>>() {}
                    );

            System.out.println(
                    "Number of attributes extracted: "
                            + attributes.size()
            );

            productAttributeRepository.deleteAll(
                    productAttributeRepository.findByProductId(productId)
            );

            for (AIAttributeResponse attribute : attributes) {

                ProductAttribute productAttribute =
                        new ProductAttribute();

                productAttribute.setAttributeName(
                        attribute.getAttributeName()
                );

                productAttribute.setAttributeValue(
                        attribute.getAttributeValue()
                );

                productAttribute.setConfidence(
                        attribute.getConfidence()
                );

                productAttribute.setEvidence(
                        attribute.getEvidence()
                );

                productAttribute.setProduct(product);

                productAttributeRepository.save(productAttribute);

                System.out.println(
                        "Saved attribute: "
                                + attribute.getAttributeName()
                                + " → "
                                + attribute.getAttributeValue()
                );
            }

            return productRepository.findById(productId)
                    .orElseThrow();

        } catch (Exception e) {

            System.out.println("Gemini API error:");
            e.printStackTrace();

            throw new RuntimeException(
                    "AI enrichment failed",
                    e
            );
        }
    }

    public NaturalLanguageSearchResponse understandSearchQuery(
            String query) {

        try (Client client = new Client()) {

            String prompt = """
                    
                    You are a product search query analyzer.
                    
                    Convert the user's search query into ONE attribute and value.
                    
                    IMPORTANT:
                    Use the exact attribute names used by the product database.
                    
                    Allowed attribute names:
                    - Power
                    - Material
                    - Pump Type
                    - Flow Rate
                    
                    Map common variations to the correct database attribute.
                    
                    Examples:
                    
                    "10 HP pump"
                    →
                    {
                      "attribute": "Power",
                      "value": "10 HP"
                    }
                    
                    "10 horsepower pump"
                    →
                    {
                      "attribute": "Power",
                      "value": "10 HP"
                    }
                    
                    "high power pump"
                    →
                    {
                      "attribute": "Power",
                      "value": "high power"
                    }
                    
                    "stainless steel pump"
                    →
                    {
                      "attribute": "Material",
                      "value": "stainless steel"
                    }
                    
                    "centrifugal pump"
                    →
                    {
                      "attribute": "Pump Type",
                      "value": "centrifugal"
                    }
                    
                    "pump with 800 L/min"
                    →
                    {
                      "attribute": "Flow Rate",
                      "value": "800 L/min"
                    }
                    
                    User query:
                    %s
                    
                    Return ONLY valid JSON in exactly this format:
                    
                    {
                      "attribute": "attribute name",
                      "value": "attribute value"
                    }
                    
                    Do not add explanations.
                    Do not use markdown.
                    Do not invent information.
                    
                    
                    """.formatted(query);

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.1-flash-lite",
                            prompt,
                            null
                    );

            String json = response.text()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    json,
                    NaturalLanguageSearchResponse.class
            );

        } catch (Exception e) {

            System.out.println(
                    "Natural language search error:"
            );

            e.printStackTrace();

            return null;
        }
    }

    public NaturalLanguageMultiSearchResponse understandMultiSearchQuery(
            String query) {

        try (Client client = new Client()) {

            String prompt = """
                    You are a product search query analyzer.

                    Convert the user's natural language query into
                    ALL attribute/value filters required for the search.

                    User query:
                    %s

                    Examples:

                    "find pumps with 5 HP"

                    [
                      {
                        "attribute": "Power",
                        "value": "5 HP"
                      }
                    ]

                    "find stainless steel centrifugal pumps with 5 HP"

                    [
                      {
                        "attribute": "Power",
                        "value": "5 HP"
                      },
                      {
                        "attribute": "Material",
                        "value": "stainless steel"
                      },
                      {
                        "attribute": "Pump Type",
                        "value": "centrifugal"
                      }
                    ]

                    Return ONLY a JSON array.
                    Do not add explanations.
                    Do not use markdown.
                    Do not invent information.
                    """.formatted(query);

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.1-flash-lite",
                            prompt,
                            null
                    );

            String json = response.text()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            List<NaturalLanguageSearchResponse> filters =
                    objectMapper.readValue(
                            json,
                            new TypeReference<
                                    List<NaturalLanguageSearchResponse>
                                    >() {}
                    );

            NaturalLanguageMultiSearchResponse result =
                    new NaturalLanguageMultiSearchResponse();

            result.setFilters(filters);

            return result;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to understand multi-attribute search query"
            );
        }
    }

    public String generateComparisonSummary(
            ProductComparisonResponse comparison) {

        try (Client client = new Client()) {

            /*
             * Convert the comparison list into real JSON.
             *
             * Without this, Java would send values such as:
             *
             * ComparisonItem@5d5a3914
             *
             * instead of the actual comparison data.
             */
            String comparisonJson =
                    objectMapper.writeValueAsString(
                            comparison.getComparison()
                    );

            String prompt = """
                    You are an industrial product comparison assistant.

                    Compare the following two products.

                    Product 1:
                    %s

                    Product 2:
                    %s

                    Comparison:
                    %s

                    Give a concise professional comparison.

                    Mention:
                    - important differences
                    - important similarities
                    - which product appears better for higher performance
                    - which product may be preferable based on material

                    Do NOT invent specifications.
                    Only use the information provided.

                    Return plain text only.
                    """.formatted(
                    comparison.getProduct1(),
                    comparison.getProduct2(),
                    comparisonJson
            );

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.1-flash-lite",
                            prompt,
                            null
                    );

            return response.text();

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to generate AI comparison summary",
                    e
            );
        }
    }
}

