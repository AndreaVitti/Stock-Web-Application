package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public record HistoryDTO (
        @JsonProperty("timestamp") Date timestamp,
        @JsonProperty("highPrice") BigDecimal highPrice,
        @JsonProperty("openPrice") BigDecimal openPrice,
        @JsonProperty("closePrice")BigDecimal closePrice,
        @JsonProperty("lowPrice") BigDecimal lowPrice
){
}
