package com.javaprgraming.javaproject.table;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity 
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    // ⭐ [추가] register.html에서 보냄
    private String description;
    
    private String category;
    
    @Enumerated(EnumType.STRING) 
    private ItemStatus status;
    
    private LocalDateTime auctionEndTime;
    
    private Long startPrice;
    
    private Long currentPrice;
    
    // ⭐ [추가] register.html에서 보냄
    private Long buyNowPrice;

    // ⭐ [추가] register.html에서 보냄
    private Long bidIncrement;
    
    // ⭐ [추가] 이미지 경로
    private String imageUrl;

    // '다대일' 관계: 판매자(User)
    @ManyToOne 
    private User seller;

    // ⭐ [추가] DB 오류 해결용
    private String sellerUsername;

    // --- 이하 Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // ⭐ [추가]
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getAuctionEndTime() {
        return auctionEndTime;
    }

    public void setAuctionEndTime(LocalDateTime auctionEndTime) {
        this.auctionEndTime = auctionEndTime;
    }

    public Long getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Long startPrice) {
        this.startPrice = startPrice;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    // ⭐ [추가]
    public Long getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(Long buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    // ⭐ [추가]
    public Long getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(Long bidIncrement) {
        this.bidIncrement = bidIncrement;
    }
    
    // ⭐ [추가]
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    // ⭐ [추가]
    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
}