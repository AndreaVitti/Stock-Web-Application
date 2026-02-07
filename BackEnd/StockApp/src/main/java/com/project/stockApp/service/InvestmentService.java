package com.project.stockApp.service;

import com.project.stockApp.dto.InvestmentReq;
import com.project.stockApp.dto.Response;

public interface InvestmentService {
    Response addInvestment(InvestmentReq investmentReq);

    Response getAllInvestment();

    Response getInvestmentsBySym(String symbol);

    Response deleteInvestment(String idCode);
}
