package com.radarecom.radarecom.search.entity;

import com.radarecom.radarecom.enums.ProductType;
import com.radarecom.radarecom.search.enums.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "PRODUCTS")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private ProductType type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "SALES")
    private Integer sales;

    @Column(name = "URL")
    private String url;

    @Column(name = "PIC_URL")
    private String picUrl;

    @Column(name = "SELLER_ID")
    private String sellerId;

    @Column(name = "SELLER_STORE_URL")
    private String sellerStoreUrl;

    @Column(name = "SELLER_NICKNAME")
    private String sellerNickname;

    @Column(name = "OFFICIAL_STORE_ID")
    private String officialStoreId;

    @Column(name = "STOCK")
    private Integer stock;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CITY")
    private String city;

    @Column(name = "MEDAL")
    private String medal;

    @Column(name = "SELLER_LEVEL")
    private String sellerLevel;

    @Column(name = "AD_TYPE")
    private String adType;

    @Column(name = "LOGISTIC_TYPE")
    private String logisticType;

    @Column(name = "IS_INTERNATIONAL")
    private Boolean isInternational;

    @Column(name = "FREE_SHIPPING")
    private Boolean freeShipping;

    @Column(name = "ROOT_CATEGORY_ID")
    private String rootCategoryId;

    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Column(name = "VISITS")
    private Long visits;

    @Column(name = "VISITS_LAST_UPDATE")
    private LocalDate visitsLastUpdate;

    @Column(name = "ITEM_ID")
    private String itemId;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @Column(name = "RADAR_LAST_UPDATE")
    private LocalDate radarLastUpdate;

    @Column(name = "SELLER_INFO_LAST_UPDATE")
    private LocalDate sellerInfoLastUpdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER")
    private Provider provider;

    @Transient
    public Double getTotalRevenue() {
        return sales * price;
    }

    @Transient
    public Double getDailyRevenue() {
        var days = ChronoUnit.DAYS.between(createdAt, LocalDate.now());
        if (days == 0) days = 1;
        return getTotalRevenue() / days;
    }

    @Transient
    public Double getMonthlySalesAverage() {
        var months = ChronoUnit.MONTHS.between(createdAt, LocalDate.now());
        if (months == 0) months = 1;
        return (double) (sales / months);
    }

}
