package com.project.backend.service.impl;

import com.project.backend.dto.HistoryDTO;
import com.project.backend.dto.Response;
import com.project.backend.dto.StockDTO;
import com.project.backend.service.StockService;
import com.project.backend.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    @Value("${client.base-Url}")
    private String baseUrl;

    @Override
    public Response getCurrent(String symbol) {
        Response response = new Response();
        JsonNode yahooApiResp = Utility.getYahooFinanceResp(symbol, "1d", "1d", baseUrl);
        JsonNode metadata = yahooApiResp.path("chart")
                .path("result")
                .get(0)
                .path("meta");
        String longname;
        if (symbol.contains(".") && symbol.split("\\.")[1].equals("F")) {
            longname = "Bond (no name)";
        } else {
            longname = metadata.get("longName").asString();
        }
        response.setStockDTO(new StockDTO(
                metadata.get("symbol").asString(),
                longname,
                metadata.get("currency").asString()
        ));
        BigDecimal currentPrice = metadata.get("regularMarketPrice").asDecimal();
        response.setCurrentPrice(currentPrice);
        BigDecimal prevClose = metadata.get("chartPreviousClose").asDecimal();
        BigDecimal diff = currentPrice.subtract(prevClose);
        BigDecimal percentage = ((diff.multiply(new BigDecimal(100))).divide(prevClose, 2, RoundingMode.HALF_UP));
        response.setPercentage(percentage);
        response.setTimestamp(LocalDate.now());
        response.setStatus(200);
        return response;
    }

    @Override
    public Response getHistory(String symbol) {
        Response response = new Response();
        JsonNode yahooApiResp = Utility.getYahooFinanceResp(symbol, "10y", "1d", baseUrl);
        JsonNode resultObj = yahooApiResp.path("chart")
                .path("result")
                .get(0);
        JsonNode timestamps = resultObj.path("timestamp");
        JsonNode quoteObj = resultObj.path("indicators")
                .path("quote")
                .get(0);
        List<HistoryDTO> historyDTOS = new ArrayList<>();
        for (int i = 0; i < timestamps.size(); i++) {
            HistoryDTO historyDTO = new HistoryDTO(new Date((timestamps.get(i).asLong()) * 1000),
                    quoteObj.get("high").get(i).asDecimal(),
                    quoteObj.get("open").get(i).asDecimal(),
                    quoteObj.get("low").get(i).asDecimal(),
                    quoteObj.get("close").get(i).asDecimal());
            historyDTOS.add(historyDTO);
        }
        response.setHistoryDTOS(historyDTOS);
        response.setStatus(200);
        return response;
    }
}
