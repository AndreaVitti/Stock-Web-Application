package com.project.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvestmentReq {
    @NotBlank
    private String symbol;
    private BigDecimal investedCapital;
    private LocalDateTime buyDate;
    private BigDecimal buyPrice;
    @NotBlank
    private String currency;
}
