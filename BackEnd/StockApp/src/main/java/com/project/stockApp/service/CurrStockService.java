package com.project.stockApp.service;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;

public interface CurrStockService {
    Response getStockPrice(String symbol);

    Response getStockHistory(String symbol);
}