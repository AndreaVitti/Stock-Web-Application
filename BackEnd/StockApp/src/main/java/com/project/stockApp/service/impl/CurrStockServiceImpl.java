package com.project.stockApp.service.impl;

import com.project.stockApp.dto.*;
import com.project.stockApp.service.CurrStockService;
import com.project.stockApp.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CurrStockServiceImpl implements CurrStockService {

    @Value("${client.base-Url}")
    private String baseUrl;

    @Override
    public Response getStockPrice(String symbol) {
        Response response = new Response();
        JSONObject resData = Utility.yahooFinClientRange(symbol, "1d", "1d", baseUrl);
        JSONObject meta = resData.getJSONObject("meta");
        response.setCurrency(meta.getString("currency"));
        response.setSymbol(meta.getString("symbol"));
        if (symbol.contains(".") && symbol.split("\\.")[1].equals("F")) {
            response.setLongName("Bond (no name)");
        } else {
            response.setLongName(meta.getString("longName"));
        }
        BigDecimal price = meta.getBigDecimal("regularMarketPrice");
        response.setPrice(price);
        BigDecimal prevClose = meta.getBigDecimal("chartPreviousClose");
        BigDecimal diff = price.subtract(prevClose);
        BigDecimal percentage = ((diff.multiply(new BigDecimal(100))).divide(prevClose, 2, RoundingMode.HALF_UP));
        response.setPercentage(percentage);
        ZonedDateTime italy = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        response.setCurrentDate(italy.toLocalDateTime());
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response getStockHistory(String symbol) {
        Response response = new Response();
        JSONObject resData = Utility.yahooFinClientRange(symbol, "10y", "1d", baseUrl);
        JSONArray timestamp = Utility.getOHCLplusDate(resData).get(0);
        JSONArray high = Utility.getOHCLplusDate(resData).get(1);
        JSONArray open = Utility.getOHCLplusDate(resData).get(2);
        JSONArray close = Utility.getOHCLplusDate(resData).get(3);
        JSONArray low = Utility.getOHCLplusDate(resData).get(4);
        List<HistoryQuoteDTO> historyQuoteDTOS = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            List<BigDecimal> indicators = Utility.bigDecimalBuilder(high.get(i).toString(), open.get(i).toString(), close.get(i).toString(), low.get(i).toString());
            historyQuoteDTOS.add(new HistoryQuoteDTO(
                    new Date(Long.parseLong(timestamp.get(i).toString()) * 1000),
                    indicators.get(0),
                    indicators.get(1),
                    indicators.get(2),
                    indicators.get(3))
            );
        }
        response.setHistoryQuoteDTO(historyQuoteDTOS);
        ZonedDateTime italy = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        response.setCurrentDate(italy.toLocalDateTime());
        response.setHttpCode(200);
        return response;
    }
}