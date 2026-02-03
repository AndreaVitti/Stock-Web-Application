package com.project.stockApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentDTO(
        @JsonProperty("idCode") String identificationCode,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("capital") BigDecimal capital,
        @JsonProperty("date") LocalDateTime initDate,
        @JsonProperty("buyPrice") BigDecimal initPrice,
        @JsonProperty("currency") String currency
) {
}
