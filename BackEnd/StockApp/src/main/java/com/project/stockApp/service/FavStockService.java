package com.project.stockApp.service;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.Response;

public interface FavStockService {
    Response addFavourite(FavouriteReq favouriteReq);

    Response getAllFavourites();

    Response getFavourite(String symbol);

    Response removeFavourite(String symbol);

    Response refreshFavourite(String symbol);
}
