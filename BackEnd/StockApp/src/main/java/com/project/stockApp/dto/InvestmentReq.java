package com.project.stockApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentReq {
    private String symbol;
    private BigDecimal capital;
    private LocalDateTime initDate;
    private BigDecimal initPrice;
    private String currency;
}
