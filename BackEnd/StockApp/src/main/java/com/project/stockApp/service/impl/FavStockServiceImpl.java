package com.project.stockApp.service.impl;

import com.project.stockApp.dto.FavouriteReq;
import com.project.stockApp.dto.HistoryQuoteDTO;
import com.project.stockApp.dto.Response;
import com.project.stockApp.dto.StockDTO;
import com.project.stockApp.entity.FavStock;
import com.project.stockApp.entity.HistoryQuote;
import com.project.stockApp.exception.ResourceNotFound;
import com.project.stockApp.repository.FavouriteRepository;
import com.project.stockApp.repository.HistoryQuoteRepository;
import com.project.stockApp.repository.InvestmentRepository;
import com.project.stockApp.service.FavStockService;
import com.project.stockApp.utility.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavStockServiceImpl implements FavStockService {

    @Value("${client.base-Url}")
    private String baseUrl;

    private final FavouriteRepository favouriteRepository;
    private final HistoryQuoteRepository historyQuoteRepository;

    @Override
    public Response addFavourite(FavouriteReq favouriteReq) {
        Response response = new Response();
        FavStock favStock = new FavStock();
        JSONObject resData = Utility.yahooFinClientRange(favouriteReq.getSymbol(), "10y", "1d", baseUrl);
        JSONObject meta = resData.getJSONObject("meta");
        favStock.setCurrency(meta.getString("currency"));
        favStock.setSymbol(meta.getString("symbol"));
        if (favouriteReq.getSymbol().contains(".") && favouriteReq.getSymbol().split("\\.")[1].equals("F")) {
            favStock.setName("Bond (no name)");
        } else {
            favStock.setName(meta.getString("longName"));
        }
        JSONArray timestamp = Utility.getOHCLplusDate(resData).get(0);
        JSONArray high = Utility.getOHCLplusDate(resData).get(1);
        JSONArray open = Utility.getOHCLplusDate(resData).get(2);
        JSONArray close = Utility.getOHCLplusDate(resData).get(3);
        JSONArray low = Utility.getOHCLplusDate(resData).get(4);
        favouriteRepository.save(favStock);
        List<HistoryQuote> historyQuotes = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            Utility.historyQuoteEntMapper(favStock, timestamp, high, open, close, low, historyQuotes, i);
        }
        historyQuoteRepository.saveAll(historyQuotes);
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response getAllFavourites() {
        Response response = new Response();
        List<StockDTO> stockDTOS = favouriteRepository.findAll()
                .stream()
                .map(favStock -> new StockDTO(favStock.getSymbol(),
                        favStock.getName(),
                        favStock.getCurrency()))
                .toList();
        response.setStockDTOS(stockDTOS);
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response getFavourite(String symbol) {
        Response response = new Response();
        FavStock favStock = favouriteRepository.findBySymbol(symbol).orElse(null);
        if (favStock == null) {
            throw new ResourceNotFound("The requested resource was not found");
        }
        response.setCurrency(favStock.getCurrency());
        response.setSymbol(favStock.getSymbol());
        response.setLongName(favStock.getName());
        List<HistoryQuoteDTO> historyQuoteDTOS = historyQuoteRepository
                .findByStockId(favStock.getId())
                .stream()
                .map(historyQuote -> new HistoryQuoteDTO(historyQuote.getDate(),
                        historyQuote.getHigh(),
                        historyQuote.getOpen(),
                        historyQuote.getClose(),
                        historyQuote.getLow()))
                .toList();
        response.setHistoryQuoteDTO(historyQuoteDTOS);
        response.setHttpCode(200);
        return response;
    }

    @Override
    @Transactional
    public Response removeFavourite(String symbol) {
        Response response = new Response();
        try {
            favouriteRepository.deleteBySymbol(symbol);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response refreshFavourite(String symbol) {
        Response response = new Response();
        FavStock favStock = favouriteRepository.findBySymbol(symbol).orElse(null);
        if (favStock == null) {
            throw new ResourceNotFound("The requested resource was not found");
        }
        Long stockId = favStock.getId();
        HistoryQuote lastHistoryQuote = historyQuoteRepository.findLastQuoteByStock(stockId);
        Date lastDate = lastHistoryQuote.getDate();
        long lastDateUnix = lastDate.getTime() / 1000;
        long nowUnix = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
        RestClient restClient = Utility.yahooFinClientBuilder(baseUrl, symbol);
        String yahResp = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("period1", lastDateUnix)
                        .queryParam("period2", nowUnix)
                        .queryParam("interval", "1d")
                        .build())
                .retrieve()
                .body(String.class);
        JSONObject yahRespJson = new JSONObject(yahResp);
        JSONObject chart = yahRespJson.getJSONObject("chart");
        JSONArray result = chart.getJSONArray("result");
        JSONObject resData = result.getJSONObject(0);
        JSONArray timestamp = Utility.getOHCLplusDate(resData).get(0);
        JSONArray high = Utility.getOHCLplusDate(resData).get(1);
        JSONArray open = Utility.getOHCLplusDate(resData).get(2);
        JSONArray close = Utility.getOHCLplusDate(resData).get(3);
        JSONArray low = Utility.getOHCLplusDate(resData).get(4);
        List<HistoryQuote> historyQuotes = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            ZoneId zone = ZoneId.of("UTC");
            LocalDate date1 = Instant.ofEpochSecond(Long.parseLong(timestamp.get(i).toString())).atZone(zone).toLocalDate();
            LocalDate date2 = Instant.ofEpochSecond(lastDateUnix).atZone(zone).toLocalDate();
            List<BigDecimal> indicators = Utility.bigDecimalBuilder(high.get(i).toString(), open.get(i).toString(), close.get(i).toString(), low.get(i).toString());
            if (date2.equals(date1)) {
                lastHistoryQuote.setHigh(indicators.get(0));
                lastHistoryQuote.setOpen(indicators.get(1));
                lastHistoryQuote.setClose(indicators.get(2));
                lastHistoryQuote.setLow(indicators.get(3));
                historyQuoteRepository.save(lastHistoryQuote);
                continue;
            }
            Utility.historyQuoteEntMapper(favStock, timestamp, high, open, close, low, historyQuotes, i);
        }
        historyQuoteRepository.saveAll(historyQuotes);
        response.setHttpCode(200);
        return response;
    }
}
