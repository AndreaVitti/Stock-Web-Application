package com.project.stockApp.repository;

import com.project.stockApp.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment,Long > {
    List<Investment> findAllBySymbol(String symbol);

    void deleteByIdentificationCode(String idCode);
}
