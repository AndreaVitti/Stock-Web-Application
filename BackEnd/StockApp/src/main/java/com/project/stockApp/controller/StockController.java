package com.project.stockApp.controller;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;
import com.project.stockApp.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:4201")
public class StockController {
    private final StockService stockService;

    @GetMapping("/get/price/{symbol}")
    public ResponseEntity<Response> getStockPrice(@PathVariable String symbol) {
        Response response = stockService.getStockPrice(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/get/history/{symbol}")
    public ResponseEntity<Response> getStockHistory(@PathVariable String symbol) {
        Response response = stockService.getStockHistory(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/favourite/add")
    public ResponseEntity<Response> addFavourite(@RequestBody FavouriteReq favouriteReq){
        Response response = stockService.addFavourite(favouriteReq);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/favourite/get/all")
    public ResponseEntity<Response> getAllFavourites(){
        Response response = stockService.getAllFavourites();
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/favourite/get/{symbol}")
    public ResponseEntity<Response> getFavourite(@PathVariable String symbol){
        Response response = stockService.getFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/favourite/delete/{symbol}")
    public ResponseEntity<Response> removeFavourite(@PathVariable String symbol){
        Response response = stockService.removeFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PatchMapping("/favourite/refresh/{symbol}")
    public ResponseEntity<Response> refrshFavourite(@PathVariable String symbol){
        Response response = stockService.refreshFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PostMapping("/investment/add")
    public ResponseEntity<Response> addInvestment(@RequestBody InvestmentReq investmentReq) {
        Response response = stockService.addInvestment(investmentReq);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/investment/get/all")
    public ResponseEntity<Response> getAllInvestment() {
        Response response = stockService.getAllInvestment();
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/investment/get/{symbol}")
    public ResponseEntity<Response> getInvestment(@PathVariable String symbol) {
        Response response = stockService.getInvestmentsBySym(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/investment/delete/{idCode}")
    public ResponseEntity<Response> deleteInvestment(@PathVariable String idCode) {
        Response response = stockService.deleteInvestment(idCode);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
}