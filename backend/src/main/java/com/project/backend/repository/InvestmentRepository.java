package com.project.backend.repository;

import com.project.backend.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvestmentRepository  extends JpaRepository<Investment, Long> {
    List<Investment> findAllBySymbol(String symbol);

    void deleteByIdentificationCode(UUID idCode);

    boolean existsByIdentificationCode(UUID idCode);
}
