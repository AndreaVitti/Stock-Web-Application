package com.project.stockApp.controller;

import com.project.stockApp.dto.Response;
import com.project.stockApp.service.CurrStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:4201")
public class CurrStockController {
    private final CurrStockService currStockService;

    @GetMapping("/get/price/{symbol}")
    public ResponseEntity<Response> getStockPrice(@PathVariable String symbol) {
        Response response = currStockService.getStockPrice(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/get/history/{symbol}")
    public ResponseEntity<Response> getStockHistory(@PathVariable String symbol) {
        Response response = currStockService.getStockHistory(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
}