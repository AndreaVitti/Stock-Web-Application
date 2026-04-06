package com.project.backend.service.impl;

import com.project.backend.dto.InvestmentReq;
import com.project.backend.dto.Response;
import com.project.backend.entity.Investment;
import com.project.backend.exception.ResourceNotFound;
import com.project.backend.repository.InvestmentRepository;
import com.project.backend.service.InvestmentService;
import com.project.backend.utility.Mapper;
import com.project.backend.utility.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        investment.setIdentificationCode(UUID.randomUUID());
        investment.setSymbol(investmentReq.getSymbol());
        if (investmentReq.getInvestedCapital() == null) {
            investment.setInvestedCapital(new BigDecimal(1));
        } else {
            investment.setInvestedCapital(investmentReq.getInvestedCapital());
        }
        if (investmentReq.getBuyDate() == null) {
            investment.setBuyDate(LocalDateTime.now());
        } else {
            investment.setBuyDate(investmentReq.getBuyDate());
        }
        if (investmentReq.getBuyPrice() == null) {
            JsonNode yahooApiResp = Utility.getYahooFinanceResp(investmentReq.getSymbol(), "1d", "1d", baseUrl);
            JsonNode metadata = yahooApiResp.path("chart")
                    .path("result")
                    .get(0)
                    .path("meta");
            investment.setBuyPrice(metadata.get("regularMarketPrice").asDecimal());
        } else {
            investment.setBuyPrice(investmentReq.getBuyPrice());
        }
        investment.setCurrency(investmentReq.getCurrency());
        investmentRepository.save(investment);
        response.setStatus(200);
        return response;
    }

    @Override
    public Response getAllInvestment() {
        Response response = new Response();
        List<Investment> investments = investmentRepository.findAll();
        response.setInvestmentDTOS(
                Mapper.mapInvestmentsToInvestmentDTOS(investments)
        );
        response.setStatus(200);
        return response;
    }

    @Override
    public Response getInvestmentsBySym(String symbol) {
        Response response = new Response();
        List<Investment> investments = investmentRepository.findAllBySymbol(symbol);
        response.setInvestmentDTOS(
                Mapper.mapInvestmentsToInvestmentDTOS(investments)
        );
        response.setStatus(200);
        return response;
    }

    @Override
    @Transactional
    public Response deleteInvestment(String idCode) {
        Response response = new Response();
        if(investmentRepository.existsByIdentificationCode(UUID.fromString(idCode))){
            investmentRepository.deleteByIdentificationCode(UUID.fromString(idCode));
        } else{
            throw new ResourceNotFound("Investment to delete not found");
        }
        response.setStatus(200);
        return response;
    }
}
