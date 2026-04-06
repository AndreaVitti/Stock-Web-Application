package com.project.backend.repository;

import com.project.backend.entity.FavStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavStockRepository extends JpaRepository<FavStock, Long>{
    Optional<FavStock> findBySymbol(String symbol);

    void deleteBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
