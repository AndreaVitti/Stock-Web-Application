package com.project.stockApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StockDTO(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("name") String name,
        @JsonProperty("currency") String currency) {
}
