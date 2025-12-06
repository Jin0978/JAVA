package com.javaprgraming.javaproject.dto;

public class BidResponse {
    private String bidderName; // 입찰자 이름
    private Long newPrice;     // 갱신된 현재가
    private String bidTime;    // 입찰 시간

    public BidResponse(String bidderName, Long newPrice, String bidTime) {
        this.bidderName = bidderName;
        this.newPrice = newPrice;
        this.bidTime = bidTime;
    }

    // Getter & Setter
    public String getBidderName() { return bidderName; }
    public Long getNewPrice() { return newPrice; }
    public String getBidTime() { return bidTime; }
}