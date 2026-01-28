package com.radarecom.radarecom.search.entity;

import com.radarecom.radarecom.enums.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "SEARCH_CACHE_ITEM")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchCacheItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "search_cache_item_id_seq")
    @SequenceGenerator(
            name = "search_cache_item_id_seq",
            sequenceName = "search_cache_item_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "PRODUCT_ID", nullable = false, length = 100)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private ProductType type;

    @Column(name = "URL", nullable = false)
    private String url;

}
