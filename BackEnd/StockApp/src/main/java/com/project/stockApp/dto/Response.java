package com.project.stockApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int httpCode;
    private List<StockDTO> stockDTOS;
    private List<HistoryQuoteDTO> historyQuoteDTO;
    private String currency;
    private String symbol;
    private String longName;
    private BigDecimal price;
    private BigDecimal percentage;
    private LocalDateTime currentDate;
    private String message;
    private List<InvestmentDTO> investmentDTOS;
}
