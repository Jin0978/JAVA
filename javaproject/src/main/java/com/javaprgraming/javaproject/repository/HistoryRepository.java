package com.javaprgraming.javaproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javaprgraming.javaproject.table.History;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByBuyer_Id(Long buyerId);

    List<History> findBySeller_Id(Long sellerId);
}
