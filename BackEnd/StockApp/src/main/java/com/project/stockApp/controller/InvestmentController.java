package com.project.stockApp.controller;

import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;
import com.project.stockApp.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:4201")
public class InvestmentController {
    private final InvestmentService investmentService;

    @PostMapping("/investment/add")
    public ResponseEntity<Response> addInvestment(@RequestBody InvestmentReq investmentReq) {
        Response response = investmentService.addInvestment(investmentReq);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/investment/get/all")
    public ResponseEntity<Response> getAllInvestment() {
        Response response = investmentService.getAllInvestment();
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/investment/get/{symbol}")
    public ResponseEntity<Response> getInvestment(@PathVariable String symbol) {
        Response response = investmentService.getInvestmentsBySym(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/investment/delete/{idCode}")
    public ResponseEntity<Response> deleteInvestment(@PathVariable String idCode) {
        Response response = investmentService.deleteInvestment(idCode);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
}
