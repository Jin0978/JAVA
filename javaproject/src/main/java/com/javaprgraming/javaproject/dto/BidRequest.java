package com.javaprgraming.javaproject.dto;
//dto 를 쓰는 이유
// 보안: 내 회원 정보(비번, 번호)를 남들에게 다 보여주지 않기 위해 씁니다.

// 맞춤형: 화면에 딱 필요한 데이터(이름, 가격)만 골라서 예쁘게 보내주려고 씁니다.

// 안전: DB 구조가 바뀌어도 화면(프론트) 쪽은 에러가 안 나게 보호해 줍니다.

public class BidRequest {
    private Long auctionId;  // 경매 물품 번호
    private Long bidderId;   // 입찰자(유저) 번호
    private Long bidAmount;  // 입찰 금액

    // Getter & Setter
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
    public Long getBidAmount() { return bidAmount; }
    public void setBidAmount(Long bidAmount) { this.bidAmount = bidAmount; }
}