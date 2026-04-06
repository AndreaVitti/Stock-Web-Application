package com.project.backend.controller;

import com.project.backend.dto.InvestmentReq;
import com.project.backend.dto.Response;
import com.project.backend.service.InvestmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/investment")
public class InvestmentController {
    private final InvestmentService investmentService;

    @PostMapping("/add")
    public ResponseEntity<Response> addInvestment(@RequestBody @Valid InvestmentReq investmentReq) {
        Response response = investmentService.addInvestment(investmentReq);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/get/all")
    public ResponseEntity<Response> getAllInvestment() {
        Response response = investmentService.getAllInvestment();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/get/{symbol}")
    public ResponseEntity<Response> getInvestment(@PathVariable @NotBlank String symbol) {
        Response response = investmentService.getInvestmentsBySym(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{idCode}")
    public ResponseEntity<Response> deleteInvestment(@PathVariable @NotBlank String idCode) {
        Response response = investmentService.deleteInvestment(idCode);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
