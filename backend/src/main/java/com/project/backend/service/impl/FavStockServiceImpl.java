package com.project.backend.service.impl;

import com.project.backend.dto.FavStockReq;
import com.project.backend.dto.HistoryDTO;
import com.project.backend.dto.Response;
import com.project.backend.dto.StockDTO;
import com.project.backend.entity.FavStock;
import com.project.backend.entity.History;
import com.project.backend.exception.ResourceNotFound;
import com.project.backend.repository.FavStockRepository;
import com.project.backend.repository.HistoryRepository;
import com.project.backend.service.FavStockService;
import com.project.backend.utility.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
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

    private final FavStockRepository favStockRepository;
    private final HistoryRepository historyRepository;

    @Override
    public Response addFavourite(FavStockReq favStockReq) {
        Response response = new Response();
        FavStock favStock = new FavStock();
        JsonNode yahooApiResp = Utility.getYahooFinanceResp(favStockReq.getSymbol(), "10y", "1d", baseUrl);
        JsonNode resultObj = yahooApiResp.path("chart")
                .path("result")
                .get(0);
        JsonNode metadata = resultObj.path("meta");
        favStock.setCurrency(metadata.get("currency").asString());
        favStock.setSymbol(metadata.get("symbol").asString());
        if (favStockReq.getSymbol().contains(".") && favStockReq.getSymbol().split("\\.")[1].equals("F")) {
            favStock.setName("Bond (no name)");
        } else {
            favStock.setName(metadata.get("longName").asString());
        }
        favStockRepository.save(favStock);
        JsonNode quoteObj = resultObj.path("indicators")
                .path("quote")
                .get(0);
        JsonNode timestamps = resultObj.path("timestamp");
        List<History> histories = new ArrayList<>();
        for (int i = 0; i < timestamps.size(); i++) {
            History history = new History();
            history.setTimestamp(new Date((timestamps.get(i).asLong()) * 1000));
            history.setOpenPrice(quoteObj.get("high").get(i).asDecimal());
            history.setHighPrice(quoteObj.get("open").get(i).asDecimal());
            history.setClosePrice(quoteObj.get("low").get(i).asDecimal());
            history.setLowPrice(quoteObj.get("close").get(i).asDecimal());
            history.setFavStock(favStock);
            histories.add(history);
        }
        historyRepository.saveAll(histories);
        response.setStatus(200);
        return response;
    }

    @Override
    public Response getAllFavourites() {
        Response response = new Response();
        List<StockDTO> stockDTOS = favStockRepository.findAll()
                .stream()
                .map(favStock -> new StockDTO(favStock.getSymbol(),
                        favStock.getName(),
                        favStock.getCurrency()))
                .toList();
        response.setStockDTOS(stockDTOS);
        response.setStatus(200);
        return response;
    }

    @Override
    public Response getFavourite(String symbol) {
        Response response = new Response();
        FavStock favStock = favStockRepository.findBySymbol(symbol).orElse(null);
        if (favStock == null) {
            throw new ResourceNotFound("Favourite stock not found.");
        }
        response.setStockDTO(new StockDTO(favStock.getSymbol(), favStock.getName(), favStock.getCurrency()));
        List<HistoryDTO> historyDTOS = historyRepository
                .findByFavStockId(favStock.getId())
                .stream()
                .map(history -> new HistoryDTO(history.getTimestamp(),
                        history.getHighPrice(),
                        history.getOpenPrice(),
                        history.getClosePrice(),
                        history.getLowPrice()))
                .toList();
        response.setHistoryDTOS(historyDTOS);
        response.setStatus(200);
        return response;
    }

    @Override
    @Transactional
    public Response removeFavourite(String symbol) {
        Response response = new Response();
        if (favStockRepository.existsBySymbol(symbol)) {
            favStockRepository.deleteBySymbol(symbol);
        } else {
            throw new ResourceNotFound("Favourite stock to remove not found.");
        }
        response.setStatus(200);
        return response;
    }

    @Override
    public Response refreshFavourite(String symbol) {
        FavStock favStock = favStockRepository.findBySymbol(symbol).orElse(null);
        if (favStock == null) {
            throw new ResourceNotFound("Favourite stock to refresh not found");
        }
        Response response = new Response();
        Long favStockId = favStock.getId();
        History lastHistory = historyRepository.findLastHistoryByFavStock(favStockId);
        Date lastDate = lastHistory.getTimestamp();
        long lastDateUnix = lastDate.getTime() / 1000;
        long nowUnix = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
        RestClient restClient = Utility.yahooFinClientBuilder(baseUrl, symbol);
        JsonNode yahooApiResp = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("period1", lastDateUnix)
                        .queryParam("period2", nowUnix)
                        .queryParam("interval", "1d")
                        .build())
                .retrieve()
                .body(JsonNode.class);
        JsonNode resultObj = yahooApiResp.path("chart")
                .path("result")
                .get(0);
        JsonNode timestamps = resultObj.path("timestamp");
        JsonNode quoteObj = resultObj.path("indicators")
                .path("quote")
                .get(0);
        List<History> histories = new ArrayList<>();
        for (int i = 0; i < timestamps.size(); i++) {
            ZoneId zone = ZoneId.of("UTC");
            LocalDate date1 = Instant.ofEpochSecond(timestamps.get(i).asLong()).atZone(zone).toLocalDate();
            LocalDate date2 = Instant.ofEpochSecond(lastDateUnix).atZone(zone).toLocalDate();
            if (date2.equals(date1)) {
                setHistoryPrices(lastHistory, quoteObj, i);
                historyRepository.save(lastHistory);
                continue;
            }
            History history = new History();
            history.setTimestamp(new Date((timestamps.get(i).asLong()) * 1000));
            setHistoryPrices(history, quoteObj, i);
            history.setFavStock(favStock);
            histories.add(history);
        }
        historyRepository.saveAll(histories);
        response.setStatus(200);
        return response;
    }

    private void setHistoryPrices(History history, JsonNode quoteObj, int index) {
        history.setHighPrice(quoteObj.get("high").get(index).asDecimal());
        history.setOpenPrice(quoteObj.get("open").get(index).asDecimal());
        history.setLowPrice(quoteObj.get("low").get(index).asDecimal());
        history.setClosePrice(quoteObj.get("close").get(index).asDecimal());
    }
}
