package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;
    private BigDecimal currentPrice;
    private BigDecimal percentage;
    private LocalDate timestamp;
    private StockDTO stockDTO;
    private List<StockDTO> stockDTOS;
    private List<HistoryDTO> historyDTOS;
    private List<InvestmentDTO> investmentDTOS;
    private String message;
}
