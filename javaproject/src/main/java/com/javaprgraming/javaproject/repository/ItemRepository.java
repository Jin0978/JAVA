package com.javaprgraming.javaproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javaprgraming.javaproject.table.Item;

/**
 * 'Item' 테이블의 데이터베이스 작업을 처리하는 JPA 인터페이스입니다.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    // ⭐ [추가] 특정 판매자(sellerId)가 등록한 물건 목록 조회
    List<Item> findBySeller_Id(Long sellerId);

    List<Item> findByStatusAndAuctionEndTimeBefore(com.javaprgraming.javaproject.table.ItemStatus status,
            java.time.LocalDateTime time);

    // ⭐ [추가] 상태별 아이템 조회 (메인 페이지용)
    List<Item> findByStatus(com.javaprgraming.javaproject.table.ItemStatus status);
}