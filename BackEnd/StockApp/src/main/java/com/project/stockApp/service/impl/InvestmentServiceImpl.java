package com.project.stockApp.service.impl;

import com.project.stockApp.dto.InvestmentDTO;
import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;
import com.project.stockApp.entity.Investment;
import com.project.stockApp.repository.InvestmentRepository;
import com.project.stockApp.service.InvestmentService;
import com.project.stockApp.utility.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    @Value("${client.base-Url}")
    private String baseUrl;

    private final InvestmentRepository investmentRepository;

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
            JSONObject resData = Utility.yahooFinClientRange(investmentReq.getSymbol(), "1d", "1d", baseUrl);
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
}
