package com.project.backend.controller;

import com.project.backend.dto.FavStockReq;
import com.project.backend.dto.Response;
import com.project.backend.service.FavStockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favourite")
public class FavStockController {
    private final FavStockService favStockService;

    @PostMapping("/add")
    public ResponseEntity<Response> addFavourite(@RequestBody @Valid FavStockReq favouriteReq) {
        Response response = favStockService.addFavourite(favouriteReq);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/get/all")
    public ResponseEntity<Response> getAllFavourites() {
        Response response = favStockService.getAllFavourites();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/get/{symbol}")
    public ResponseEntity<Response> getFavourite(@PathVariable @NotBlank String symbol) {
        Response response = favStockService.getFavourite(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{symbol}")
    public ResponseEntity<Response> removeFavourite(@PathVariable @NotBlank String symbol) {
        Response response = favStockService.removeFavourite(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/refresh/{symbol}")
    public ResponseEntity<Response> refreshFavourite(@PathVariable @NotBlank String symbol) {
        Response response = favStockService.refreshFavourite(symbol);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
