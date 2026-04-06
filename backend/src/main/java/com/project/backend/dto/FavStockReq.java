package com.project.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FavStockReq {
    @NotBlank
    private String symbol;
}
