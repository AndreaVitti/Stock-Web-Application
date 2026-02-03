package com.project.stockApp.repository;

import com.project.stockApp.entity.FavStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<FavStock, Long> {
    Optional<FavStock> findBySymbol(String name);
    void deleteBySymbol(String name);
}
