package com.project.backend.repository;

import com.project.backend.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryRepository  extends JpaRepository<History, Long> {
    @Query("select q from History q where q.favStock.id = ?1 and q.timestamp = (select max(p.timestamp) from History p where p.favStock.id = ?1)")
    History findLastHistoryByFavStock(Long fav_stock_id);

    @Query("select q from History q where q.favStock.id = ?1")
    List<History> findByFavStockId(Long fav_stock_id);
}
