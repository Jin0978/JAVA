package com.javaprgraming.javaproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javaprgraming.javaproject.table.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    // ⭐ [추가] 특정 물건(itemId)의 입찰 내역을 '시간 역순(최신순)'으로 찾기
    List<Bid> findByItem_IdOrderByBidTimeDesc(Long itemId);

    // ⭐ [추가] 특정 사용자(bidderId)가 입찰한 내역 조회
    List<Bid> findByBidder_Id(Long bidderId);

    Bid findTopByItemOrderByBidAmountDesc(com.javaprgraming.javaproject.table.Item item);
}