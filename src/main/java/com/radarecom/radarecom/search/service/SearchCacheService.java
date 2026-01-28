package com.radarecom.radarecom.search.service;

import com.radarecom.radarecom.search.util.ScraperUtil;
import com.radarecom.radarecom.search.entity.SearchCache;
import com.radarecom.radarecom.search.entity.SearchCacheItem;
import com.radarecom.radarecom.search.repository.SearchCacheRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.radarecom.radarecom.search.enums.Provider.MERCADO_LIVRE;

@Service
@AllArgsConstructor
public class SearchCacheService {

    private static final Integer SEARCH_CACHE_LAST_UPDATE_LIMIT_DAYS = 2;

    private final ScraperUtil scraperUtil;
    private final SearchCacheRepository searchCacheRepository;

    public Optional<SearchCache> getSearchCacheById(String id){
        return searchCacheRepository.findById(id);
    }

    @Transactional
    public List<SearchCacheItem> getSearchItems(String url){
        Optional<SearchCache> searchCache = getSearchCacheById(url);

        List<SearchCacheItem> searchItems = new ArrayList<>();

        if (searchCache.isPresent()) {
            if (searchCache.get().getLastUpdate().isAfter(LocalDate.now().minusDays(SEARCH_CACHE_LAST_UPDATE_LIMIT_DAYS))){
                searchItems = searchCache.get().getItems();
            }else {
                var response = scraperUtil.getSearchPage(url);

                if (response.isEmpty()) return searchItems;

                searchCache.get().getItems().clear();

                response.forEach(r ->
                        searchCache.get().getItems().add(
                                SearchCacheItem.builder()
                                        .productId(r.getId())
                                        .type(r.getType())
                                        .url(r.getUrl())
                                        .build()
                        )
                );

                searchCache.get().setLastUpdate(LocalDate.now());

                searchCacheRepository.save(searchCache.get());

                searchItems = searchCache.get().getItems();
            }
        } else {
            var response = scraperUtil.getSearchPage(url);

            if (response.isEmpty()) return searchItems;

            List<SearchCacheItem> items = response.stream()
                    .map(r -> SearchCacheItem.builder()
                            .productId(r.getId())
                            .type(r.getType())
                            .url(r.getUrl())
                            .build())
                    .toList();

            SearchCache cache = SearchCache.builder()
                    .id(url)
                    .provider(MERCADO_LIVRE)
                    .lastUpdate(LocalDate.now())
                    .items(items)
                    .build();

            searchCacheRepository.save(cache);
            searchItems = items;
        }

        return searchItems;
    }

}
