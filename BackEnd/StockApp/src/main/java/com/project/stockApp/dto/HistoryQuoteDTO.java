package com.project.stockApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public record HistoryQuoteDTO(
        @JsonProperty("timestamp") Date date,
        @JsonProperty("high") BigDecimal high,
        @JsonProperty("open") BigDecimal open,
        @JsonProperty("close")BigDecimal close,
        @JsonProperty("low") BigDecimal low
) {
}
