package com.javaprgraming.javaproject.table;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 입찰한 금액
    private Long bidAmount;

    // 입찰한 시간
    private LocalDateTime bidTime;

    // '다대일' 관계: 여러개의 입찰(Bid)은 한 명의 입찰자(User)에게 속합니다.
    @ManyToOne
    private User bidder;

    // '다대일' 관계: 여러개의 입찰(Bid)은 하나의 물품(Item)에 속합니다.
    @ManyToOne
    private Item item;

    // --- 이하 Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Long bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public User getBidder() {
        return bidder;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}