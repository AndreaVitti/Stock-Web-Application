package com.project.stockApp.controller;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.Response;
import com.project.stockApp.service.FavStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:4201")
public class FavStockController {
    private final FavStockService favStockService;

    @PostMapping("/favourite/add")
    public ResponseEntity<Response> addFavourite(@RequestBody FavouriteReq favouriteReq){
        Response response = favStockService.addFavourite(favouriteReq);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/favourite/get/all")
    public ResponseEntity<Response> getAllFavourites(){
        Response response = favStockService.getAllFavourites();
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @GetMapping("/favourite/get/{symbol}")
    public ResponseEntity<Response> getFavourite(@PathVariable String symbol){
        Response response = favStockService.getFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @DeleteMapping("/favourite/delete/{symbol}")
    public ResponseEntity<Response> removeFavourite(@PathVariable String symbol){
        Response response = favStockService.removeFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @PatchMapping("/favourite/refresh/{symbol}")
    public ResponseEntity<Response> refrshFavourite(@PathVariable String symbol){
        Response response = favStockService.refreshFavourite(symbol);
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
}
