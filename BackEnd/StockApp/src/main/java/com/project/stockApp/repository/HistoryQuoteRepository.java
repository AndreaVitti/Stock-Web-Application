package com.project.stockApp.repository;

import com.project.stockApp.entity.HistoryQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryQuoteRepository extends JpaRepository<HistoryQuote, Long>{
    @Query("select q from HistoryQuote q where q.stock.id = ?1 and q.date = (select max(p.date) from HistoryQuote p where p.stock.id = ?1)")
    HistoryQuote findLastQuoteByStock(Long stock_id);

    @Query("select q from HistoryQuote q where q.stock.id = ?1")
    List<HistoryQuote> findByStockId(Long stock_id);
}
