package com.project.backend.controller;

import com.project.backend.dto.Response;
import com.project.backend.service.StockService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/get/current/{symbol}")
    public ResponseEntity<Response> getStockPrice(@PathVariable @NotBlank String symbol) {
        Response response = stockService.getCurrent(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/get/history/{symbol}")
    public ResponseEntity<Response> getStockHistory(@PathVariable @NotBlank String symbol) {
        Response response = stockService.getHistory(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
