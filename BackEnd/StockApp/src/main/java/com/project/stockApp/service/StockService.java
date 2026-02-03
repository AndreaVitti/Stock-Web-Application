package com.project.stockApp.service;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;

public interface StockService {
    Response getStockPrice(String symbol);

    Response getStockHistory(String symbol);

    Response addFavourite(FavouriteReq favouriteReq);

    Response getAllFavourites();

    Response getFavourite(String symbol);

    Response removeFavourite(String symbol);

    Response refreshFavourite(String symbol);

    Response addInvestment(InvestmentReq investmentReq);

    Response getAllInvestment();

    Response getInvestmentsBySym(String symbol);

    Response deleteInvestment(String idCode);
}