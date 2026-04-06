package com.project.backend.service;

import com.project.backend.dto.Response;
import jakarta.validation.constraints.NotBlank;

public interface StockService {
    Response getCurrent(@NotBlank String symbol);

    Response getHistory(@NotBlank String symbol);
}
