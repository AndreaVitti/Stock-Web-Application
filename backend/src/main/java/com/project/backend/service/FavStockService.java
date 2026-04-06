package com.project.backend.service;

import com.project.backend.dto.FavStockReq;
import com.project.backend.dto.Response;

public interface FavStockService {
    Response addFavourite(FavStockReq favouriteReq);

    Response getAllFavourites();

    Response getFavourite(String symbol);

    Response removeFavourite(String symbol);

    Response refreshFavourite(String symbol);
}
