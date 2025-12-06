package com.javaprgraming.javaproject.table;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Long price;

    private LocalDateTime tradeTime;

    // Constructors
    public History() {
    }

    public History(User buyer, User seller, Item item, Long price, LocalDateTime tradeTime) {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.tradeTime = tradeTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(LocalDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }
}
