package com.project.backend.service;

import com.project.backend.dto.InvestmentReq;
import com.project.backend.dto.Response;

public interface InvestmentService {
    Response addInvestment(InvestmentReq investmentReq);

    Response getAllInvestment();

    Response getInvestmentsBySym(String symbol);

    Response deleteInvestment(String idCode);
}
