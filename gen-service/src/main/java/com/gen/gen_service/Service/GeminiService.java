package com.gen.gen_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import com.gen.gen_service.DTOs.GeminiRequest;
import com.gen.gen_service.DTOs.GeminiResponse;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.model}")
    private String model;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateContent(String prompt) {
        GeminiRequest request = createRequest(prompt);
        
        GeminiResponse response = webClient.post()
                .uri(apiUrl + "/{model}:generateContent?key={key}", model, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();
        
        return extractResponseText(response);
    }

    private GeminiRequest createRequest(String prompt) {
        GeminiRequest request = new GeminiRequest();
        GeminiRequest.Content content = new GeminiRequest.Content();
        GeminiRequest.Part part = new GeminiRequest.Part();
        part.setText(prompt);
        content.setParts(Collections.singletonList(part));
        request.setContents(Collections.singletonList(content));
        return request;
    }

    private String extractResponseText(GeminiResponse response) {
        return response.getCandidates().get(0)
                .getContent().getParts().get(0)
                .getText();
    }
}