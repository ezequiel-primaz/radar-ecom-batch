package com.radarecom.radarecom.search.entity;

import com.radarecom.radarecom.search.enums.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "SEARCH_CACHE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchCache {


    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "LAST_UPDATE", nullable = false)
    private LocalDate lastUpdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", nullable = false)
    private Provider provider;

    /**
     * SearchCache é o DONO da relação.
     * Ele controla o FK SEARCH_CACHE_ID na tabela SEARCH_CACHE_ITEM.
     */
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "SEARCH_CACHE_ID",
            referencedColumnName = "ID",
            nullable = false
    )
    private List<SearchCacheItem> items;

}
