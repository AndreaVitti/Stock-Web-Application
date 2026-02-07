package com.project.stockApp.utility;

import com.project.stockApp.entity.FavStock;
import com.project.stockApp.entity.HistoryQuote;
import com.project.stockApp.exception.NoRefreshAvailable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utility {

    public static JSONObject yahooFinClientRange(String symbol, String range, String interval, String baseUrl) {
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

    public static RestClient yahooFinClientBuilder(String baseUrl, String symbol) {
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

    public static List<JSONArray> getOHCLplusDate(JSONObject data) {
        List<JSONArray> ohclPlusData = new ArrayList<>();
        JSONArray timestamp;
        try {
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

    public static void historyQuoteEntMapper(FavStock favStock, JSONArray timestamp, JSONArray high, JSONArray open, JSONArray close, JSONArray low, List<HistoryQuote> historyQuotes, int i) {
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

    public static List<BigDecimal> bigDecimalBuilder(String high, String open, String close, String low) {
        List<BigDecimal> result = new ArrayList<>();
        result.add(high.equals("null") ? null : new BigDecimal(high));
        result.add(open.equals("null") ? null : new BigDecimal(open));
        result.add(close.equals("null") ? null : new BigDecimal(close));
        result.add(low.equals("null") ? null : new BigDecimal(low));
        return result;
    }
}
