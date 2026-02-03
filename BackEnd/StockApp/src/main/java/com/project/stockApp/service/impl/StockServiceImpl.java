package com.project.stockApp.service.impl;

import com.project.stockApp.dto.*;
import com.project.stockApp.entity.HistoryQuote;
import com.project.stockApp.entity.FavStock;
import com.project.stockApp.entity.Investment;
import com.project.stockApp.exception.NoRefreshAvailable;
import com.project.stockApp.exception.ResourceNotFound;
import com.project.stockApp.repository.FavouriteRepository;
import com.project.stockApp.repository.HistoryQuoteRepository;
import com.project.stockApp.repository.InvestmentRepository;
import com.project.stockApp.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    @Value("${client.base-Url}")
    private String baseUrl;

    private final FavouriteRepository favouriteRepository;
    private final HistoryQuoteRepository historyQuoteRepository;
    private final InvestmentRepository investmentRepository;

    @Override
    public Response getStockPrice(String symbol) {
        Response response = new Response();
        JSONObject resData = yahooFinClientRange(symbol, "1d", "1d");
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
        JSONObject resData = yahooFinClientRange(symbol, "10y", "1d");
        JSONArray timestamp = getOHCLplusDate(resData).get(0);
        JSONArray high = getOHCLplusDate(resData).get(1);
        JSONArray open = getOHCLplusDate(resData).get(2);
        JSONArray close = getOHCLplusDate(resData).get(3);
        JSONArray low = getOHCLplusDate(resData).get(4);
        List<HistoryQuoteDTO> historyQuoteDTOS = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            List<BigDecimal> indicators = bigDecimalBuilder(high.get(i).toString(), open.get(i).toString(), close.get(i).toString(), low.get(i).toString());
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

    @Override
    public Response addFavourite(FavouriteReq favouriteReq) {
        Response response = new Response();
        FavStock favStock = new FavStock();
        JSONObject resData = yahooFinClientRange(favouriteReq.getSymbol(), "10y", "1d");
        JSONObject meta = resData.getJSONObject("meta");
        favStock.setCurrency(meta.getString("currency"));
        favStock.setSymbol(meta.getString("symbol"));
        if (favouriteReq.getSymbol().contains(".") && favouriteReq.getSymbol().split("\\.")[1].equals("F")) {
            favStock.setName("Bond (no name)");
        } else {
            favStock.setName(meta.getString("longName"));
        }
        JSONArray timestamp = getOHCLplusDate(resData).get(0);
        JSONArray high = getOHCLplusDate(resData).get(1);
        JSONArray open = getOHCLplusDate(resData).get(2);
        JSONArray close = getOHCLplusDate(resData).get(3);
        JSONArray low = getOHCLplusDate(resData).get(4);
        favouriteRepository.save(favStock);
        List<HistoryQuote> historyQuotes = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            historyQuoteEntMapper(favStock, timestamp, high, open, close, low, historyQuotes, i);
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
        RestClient restClient = yahooFinClientBuilder(baseUrl, symbol);
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
        JSONArray timestamp = getOHCLplusDate(resData).get(0);
        JSONArray high = getOHCLplusDate(resData).get(1);
        JSONArray open = getOHCLplusDate(resData).get(2);
        JSONArray close = getOHCLplusDate(resData).get(3);
        JSONArray low = getOHCLplusDate(resData).get(4);
        List<HistoryQuote> historyQuotes = new ArrayList<>();
        for (int i = 0; i < timestamp.length(); i++) {
            ZoneId zone = ZoneId.of("UTC");
            LocalDate date1 = Instant.ofEpochSecond(Long.parseLong(timestamp.get(i).toString())).atZone(zone).toLocalDate();
            LocalDate date2 = Instant.ofEpochSecond(lastDateUnix).atZone(zone).toLocalDate();
            List<BigDecimal> indicators = bigDecimalBuilder(high.get(i).toString(), open.get(i).toString(), close.get(i).toString(), low.get(i).toString());
            if (date2.equals(date1)) {
                lastHistoryQuote.setHigh(indicators.get(0));
                lastHistoryQuote.setOpen(indicators.get(1));
                lastHistoryQuote.setClose(indicators.get(2));
                lastHistoryQuote.setLow(indicators.get(3));
                historyQuoteRepository.save(lastHistoryQuote);
                continue;
            }
            historyQuoteEntMapper(favStock, timestamp, high, open, close, low, historyQuotes, i);
        }
        historyQuoteRepository.saveAll(historyQuotes);
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response addInvestment(InvestmentReq investmentReq) {
        Response response = new Response();
        Investment investment = new Investment();
        investment.setIdentificationCode(UUID.randomUUID().toString());
        investment.setSymbol(investmentReq.getSymbol());
        if (investmentReq.getCapital() == null) {
            investment.setCapital(new BigDecimal(1));
        } else {
            investment.setCapital(investmentReq.getCapital());
        }
        if (investmentReq.getInitDate() == null) {
            ZonedDateTime italy = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
            investment.setInitDate(italy.toLocalDateTime());
        } else {
            investment.setInitDate(investmentReq.getInitDate());
        }
        if (investmentReq.getInitPrice() == null) {
            JSONObject resData = yahooFinClientRange(investmentReq.getSymbol(), "1d", "1d");
            JSONObject meta = resData.getJSONObject("meta");
            investment.setInitPrice(meta.getBigDecimal("regularMarketPrice"));
        } else {
            investment.setInitPrice(investmentReq.getInitPrice());
        }
        investment.setCurrency(investmentReq.getCurrency());
        investmentRepository.save(investment);
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response getAllInvestment() {
        Response response = new Response();
        List<Investment> investments = investmentRepository.findAll();
        response.setInvestmentDTOS(investments.stream().map(investment ->
                        new InvestmentDTO(investment.getIdentificationCode(), investment.getSymbol(), investment.getCapital(), investment.getInitDate(), investment.getInitPrice(), investment.getCurrency()))
                .toList());
        response.setHttpCode(200);
        return response;
    }

    @Override
    public Response getInvestmentsBySym(String symbol) {
        Response response = new Response();
        List<Investment> investments = investmentRepository.findAllBySymbol(symbol);
        response.setInvestmentDTOS(investments.stream().map(investment ->
                        new InvestmentDTO(investment.getIdentificationCode(), investment.getSymbol(), investment.getCapital(), investment.getInitDate(), investment.getInitPrice(), investment.getCurrency()))
                .toList());
        response.setHttpCode(200);
        return response;
    }

    @Override
    @Transactional
    public Response deleteInvestment(String idCode) {
        Response response = new Response();
        investmentRepository.deleteByIdentificationCode(idCode);
        response.setHttpCode(200);
        return response;
    }

    private JSONObject yahooFinClientRange(String symbol, String range, String interval) {
        RestClient restClient = yahooFinClientBuilder(baseUrl, symbol);
        String yahResp = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .build())
                .retrieve()
                .body(String.class);
        JSONObject yahRespJson = new JSONObject(yahResp);
        JSONObject chart = yahRespJson.getJSONObject("chart");
        JSONArray result = chart.getJSONArray("result");
        return result.getJSONObject(0);
    }

    private RestClient yahooFinClientBuilder(String baseUrl, String symbol) {
        return RestClient.builder()
                .baseUrl(baseUrl + symbol)
                .defaultHeader(HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
                .defaultHeader(HttpHeaders.REFERER, "https://finance.yahoo.com")
                .build();
    }

    private List<JSONArray> getOHCLplusDate(JSONObject data) {
        List<JSONArray> ohclPlusData = new ArrayList<>();
        JSONArray timestamp;
        try{
            timestamp = data.getJSONArray("timestamp");
        } catch (JSONException e) {
            throw new NoRefreshAvailable("Refresh not available");
        }
        JSONObject indicators = data.getJSONObject("indicators");
        JSONArray quote = indicators.getJSONArray("quote");
        JSONObject quoteData = quote.getJSONObject(0);
        JSONArray high = quoteData.getJSONArray("high");
        JSONArray open = quoteData.getJSONArray("open");
        JSONArray close = quoteData.getJSONArray("close");
        JSONArray low = quoteData.getJSONArray("low");
        ohclPlusData.add(timestamp);
        ohclPlusData.add(high);
        ohclPlusData.add(open);
        ohclPlusData.add(close);
        ohclPlusData.add(low);
        return ohclPlusData;
    }

    private void historyQuoteEntMapper(FavStock favStock, JSONArray timestamp, JSONArray high, JSONArray open, JSONArray close, JSONArray low, List<HistoryQuote> historyQuotes, int i) {
        HistoryQuote historyQuoteNew = new HistoryQuote();
        historyQuoteNew.setStock(favStock);
        historyQuoteNew.setDate(new Date(Long.parseLong(timestamp.get(i).toString()) * 1000));
        List<BigDecimal> indicators = bigDecimalBuilder(high.get(i).toString(), open.get(i).toString(), close.get(i).toString(), low.get(i).toString());
        historyQuoteNew.setHigh(indicators.get(0));
        historyQuoteNew.setOpen(indicators.get(1));
        historyQuoteNew.setClose(indicators.get(2));
        historyQuoteNew.setLow(indicators.get(3));
        historyQuotes.add(historyQuoteNew);
    }

    private List<BigDecimal> bigDecimalBuilder(String high, String open, String close, String low) {
        List<BigDecimal> result = new ArrayList<>();
        result.add(high.equals("null") ? null : new BigDecimal(high));
        result.add(open.equals("null") ? null : new BigDecimal(open));
        result.add(close.equals("null") ? null : new BigDecimal(close));
        result.add(low.equals("null") ? null : new BigDecimal(low));
        return result;
    }
}