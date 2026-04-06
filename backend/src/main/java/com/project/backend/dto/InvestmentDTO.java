package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentDTO(
        @JsonProperty("identificationCode") String identificationCode,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("investedCapital") BigDecimal investedCapital,
        @JsonProperty("buyDate") LocalDateTime buyDate,
        @JsonProperty("buyPrice") BigDecimal buyPrice,
        @JsonProperty("currency") String currency
) {
}
