package com.project.backend.utility;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

public class Utility {
    public static JsonNode getYahooFinanceResp(String symbol, String range, String interval, String baseUrl) {
        RestClient restClient = yahooFinClientBuilder(baseUrl, symbol);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    public static RestClient yahooFinClientBuilder(String baseUrl, String symbol) {
        return RestClient.builder()
                .baseUrl(baseUrl + symbol)
                .defaultHeader(HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
                .defaultHeader(HttpHeaders.REFERER, "https://finance.yahoo.com")
                .build();
    }
}

