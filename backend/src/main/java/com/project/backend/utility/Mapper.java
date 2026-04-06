package com.project.backend.utility;

import com.project.backend.dto.InvestmentDTO;
import com.project.backend.entity.Investment;

import java.util.List;

public class Mapper {

    public static InvestmentDTO mapInvestmentToInvestmentDTO(Investment investment) {
        return new InvestmentDTO(
                investment.getIdentificationCode().toString(),
                investment.getSymbol(),
                investment.getInvestedCapital(),
                investment.getBuyDate(),
                investment.getBuyPrice(),
                investment.getCurrency());
    }

    public static List<InvestmentDTO> mapInvestmentsToInvestmentDTOS(List<Investment> investments) {
        return investments.stream().map(investment -> mapInvestmentToInvestmentDTO(investment)).toList();
    }
}
