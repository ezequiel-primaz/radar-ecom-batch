package com.radarecom.radarecom.search.service;

import com.radarecom.radarecom.enums.ProductType;
import com.radarecom.radarecom.search.dto.melidata.MelidataResponse;
import com.radarecom.radarecom.search.entity.Product;
import com.radarecom.radarecom.search.enums.LogisticTypeEnum;
import com.radarecom.radarecom.search.filter.SearchFilter;
import com.radarecom.radarecom.search.util.MLUrlBuilder;
import com.radarecom.radarecom.search.util.ScraperUtil;
import com.radarecom.radarecom.search.entity.SearchCacheItem;
import com.radarecom.radarecom.service.MLCategoryService;
import com.radarecom.radarecom.service.MercadoLivreService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.radarecom.radarecom.search.enums.Provider.MERCADO_LIVRE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class SearchService {

    private static final Logger log = LogManager.getLogger();

    private static final Integer PROCESS_PRODUCTS_CONCURRENCY = 30;
    private static final Integer FETCH_NEW_PRODUCTS_CONCURRENCY = 30;

    private static final Integer PRODUCT_LAST_UPDATE_LIMIT_DAYS = 4;
    private static final Integer PRODUCT_VISITS_LAST_UPDATE_LIMIT_DAYS = 1;
    private static final Integer SELLER_INFO_LAST_UPDATE_LIMIT_DAYS = 10;

    private final ScraperUtil scraperUtil;
    private final SearchCacheService searchCacheService;
    private final ProductService productService;
    private final MercadoLivreService mercadoLivreService;
    private final MLCategoryService mlCategoryService;

    public Flux<Product> searchStreamByKeyword(String keyword, SearchFilter searchFilter) {
        searchFilter.validateFilters();

        String finalKeyword = keyword.replace(" ", "-").toLowerCase();
        var url = MLUrlBuilder.buildSearchByKeywordUrl(finalKeyword, searchFilter);

        List<SearchCacheItem> searchItems = searchCacheService.getSearchItems(url);
        return searchStream(
                searchFilter,
                searchItems
        );
    }

    public Flux<Product> searchStreamByCategory(String categoryId, SearchFilter searchFilter) {
        searchFilter.validateFilters();
        var category = mlCategoryService.getCategoryEntity(categoryId);

        var url = MLUrlBuilder.buildSearchByCategoryUrl(category.getUrl(), searchFilter);

        List<SearchCacheItem> searchItems = searchCacheService.getSearchItems(url);
        return searchStream(
                searchFilter,
                searchItems
        );
    }

    private Flux<Product> searchStream(
            SearchFilter searchFilter,
            List<SearchCacheItem> items
    ) {
        return Flux.defer(() -> {

            var searchItems = items;

            if (searchFilter.hasTypeFilter()) {
                boolean onlyCatalog = searchFilter.isOnlyCatalog();
                searchItems = searchItems.stream()
                        .filter(item -> onlyCatalog
                                ? item.getType() == ProductType.CATALOG
                                : item.getType() != ProductType.CATALOG)
                        .toList();
            }

            List<String> ids = searchItems.stream()
                    .map(SearchCacheItem::getProductId)
                    .toList();

            List<Product> existingProducts = productService.getProductsByIds(ids);

            Set<String> existingIds = existingProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            LocalDate freshnessLimit = LocalDate.now().minusDays(PRODUCT_LAST_UPDATE_LIMIT_DAYS);

            List<Product> productsToReturn = existingProducts.stream()
                    .filter(p -> p.getRadarLastUpdate().isAfter(freshnessLimit))
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Product> productsToUpdate = existingProducts.stream()
                    .filter(p -> !p.getRadarLastUpdate().isAfter(freshnessLimit))
                    .collect(Collectors.toCollection(ArrayList::new));

            List<SearchCacheItem> productsToFetch = searchItems.stream()
                    .filter(item -> !existingIds.contains(item.getProductId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            applyFilters(productsToReturn, searchFilter);
            applyFilters(productsToUpdate, searchFilter);

            return Flux.concat(
                    processProducts(productsToReturn, this::checkProductVisits, "checkProductVisits"),
                    processProducts(productsToUpdate, this::updateProduct, "updateProduct"),
                    fetchNewProducts(productsToFetch, searchFilter)
            );
        });
    }

    private Flux<Product> processProducts(
            List<Product> products,
            Function<Product, Product> processor,
            String operationName
    ) {
        return Flux.fromIterable(products)
                .flatMap(product ->
                                Mono.fromCallable(() -> processor.apply(product))
                                        .subscribeOn(Schedulers.boundedElastic()),
                        PROCESS_PRODUCTS_CONCURRENCY
                )
                .onErrorContinue((ex, item) ->
                        log.error("Error processing product | {} | error={}", operationName, ex.getMessage(), ex)
                );
    }

    private Flux<Product> fetchNewProducts(
            List<SearchCacheItem> items,
            SearchFilter searchFilter
    ) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                                Mono.fromCallable(() -> fetchProduct(item))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .doOnError(ex -> log.error(
                                                "Error fetching product | productId={} | error={}",
                                                item.getProductId(),
                                                ex.getMessage(),
                                                ex
                                        )),
                        FETCH_NEW_PRODUCTS_CONCURRENCY
                )
                .filter(product -> applyFilters(product, searchFilter))
                .onErrorContinue((ex, item) -> {
                });
    }

    private boolean applyFilters(Product product, SearchFilter searchFilter) {

        if (searchFilter.isOnlyLogisticTypeFull()) {
            if (!LogisticTypeEnum.valueOf(product.getLogisticType().toUpperCase()).equals(LogisticTypeEnum.FULFILLMENT)) {
                return false;
            }
        }

        if (searchFilter.hasLogisticTypeFilter()) {
            if (!searchFilter.getLogisticTypes().contains(LogisticTypeEnum.valueOf(product.getLogisticType().toUpperCase()))) {
                return false;
            }
        }

        if (nonNull(searchFilter.getOnlyOfficialStore())) {
            if (searchFilter.getOnlyOfficialStore()){
                if (isNull(product.getOfficialStoreId())) {
                    return false;
                }
            }else {
                if (!isNull(product.getOfficialStoreId())) {
                    return false;
                }
            }
        }

        if (nonNull(searchFilter.getInternational())) {
            if (searchFilter.getInternational()) {
                if (!Boolean.TRUE.equals(product.getIsInternational())) {
                    return false;
                }
            }else {
                if (Boolean.TRUE.equals(product.getIsInternational())) {
                    return false;
                }
            }
        }

        if (nonNull(searchFilter.getFreeShipping())) {
            if (searchFilter.getFreeShipping()) {
                if (!Boolean.TRUE.equals(product.getFreeShipping())) {
                    return false;
                }
            }else {
                if (Boolean.TRUE.equals(product.getFreeShipping())) {
                    return false;
                }
            }
        }

        return true;
    }

    private void applyFilters(List<Product> products, SearchFilter searchFilter) {
        if (searchFilter.isOnlyLogisticTypeFull()){
            products.removeIf(p ->
                    !LogisticTypeEnum.valueOf(p.getLogisticType().toUpperCase()).equals(LogisticTypeEnum.FULFILLMENT)
            );
        }
        if (searchFilter.hasLogisticTypeFilter()) {
            products.removeIf(p ->
                    !searchFilter.getLogisticTypes().contains(
                            LogisticTypeEnum.valueOf(p.getLogisticType().toUpperCase())
                    )
            );
        }

        if (nonNull(searchFilter.getOnlyOfficialStore())){
            if (searchFilter.getOnlyOfficialStore()){
                products.removeIf(p ->
                        isNull(p.getOfficialStoreId())
                );
            }else {
                products.removeIf(p ->
                        !isNull(p.getOfficialStoreId())
                );
            }
        }

        if (nonNull(searchFilter.getInternational())){
            if (searchFilter.getInternational()){
                products.removeIf(p ->
                        !p.getIsInternational().equals(true)
                );
            }else {
                products.removeIf(p ->
                        p.getIsInternational().equals(true)
                );
            }
        }

        if (nonNull(searchFilter.getFreeShipping())){
            if (searchFilter.getFreeShipping()){
                products.removeIf(p ->
                        !p.getFreeShipping().equals(true)
                );
            }else {
                products.removeIf(p ->
                        p.getFreeShipping().equals(true)
                );
            }
        }
    }

    public MelidataResponse getProductDetailByUrl(String url){
        var melidata = scraperUtil.getProductDetail(url);
        return melidata;
    }

    //TODO criar logica de expcetion para tratar caso de algum erro no processamento de um produto, como por ex quando tentar acessar um produto inativo
    private Product fetchProduct(SearchCacheItem searchItem) {
        var melidata = scraperUtil.getProductDetail(searchItem.getUrl());

        var type = getProductType(melidata);

        var product = Product.builder()
                .id(searchItem.getProductId())
                .type(type)
                .name(melidata.getFireBaseEvent().getEventData().getName())
                .price(melidata.getMelidata().getEventDataMelidata().getPrice())
                .adType(melidata.getMelidata().getEventDataMelidata().getListingTypeId())
                .logisticType(melidata.getMelidata().getEventDataMelidata().getLogisticType())
                .freeShipping(melidata.getMelidata().getEventDataMelidata().getFreeShipping())
                .officialStoreId(melidata.getMelidata().getEventDataMelidata().getOfficialStoreId())
                .isInternational(melidata.getMelidata().getEventDataMelidata().getItemAttributes().contains("cbt"))
                .rootCategoryId(melidata.getGtmEvent().getRootCategoryId())
                .categoryId(melidata.getGtmEvent().getCategoryId())
                .url(searchItem.getUrl())
                .picUrl(melidata.getPicUrl())
                .stock(melidata.getAvailableQuantity())
                .sales(melidata.getSales())
                .radarLastUpdate(LocalDate.now())
                .provider(MERCADO_LIVRE).build();

        setSellerInfo(product, melidata.getMelidata().getEventDataMelidata().getSellerId());

        switch (type){
            case DEFAULT -> handleDefaultProduct(product, melidata);
            case USER_PRODUCT -> handleUserProduct(product, melidata);
            case CATALOG -> handleCatalogProduct(product, melidata, true);
        }

        productService.saveProduct(product);
        return product;
    }

    private Product updateProduct(Product product) {
        var melidata = scraperUtil.getProductDetail(product.getUrl());

        var type = getProductType(melidata);

        product.setName(melidata.getFireBaseEvent().getEventData().getName());
        product.setPrice(melidata.getMelidata().getEventDataMelidata().getPrice());
        product.setAdType(melidata.getMelidata().getEventDataMelidata().getListingTypeId());
        product.setLogisticType(melidata.getMelidata().getEventDataMelidata().getLogisticType());
        product.setFreeShipping(melidata.getMelidata().getEventDataMelidata().getFreeShipping());
        product.setOfficialStoreId(melidata.getMelidata().getEventDataMelidata().getOfficialStoreId());
        product.setIsInternational(melidata.getMelidata().getEventDataMelidata().getItemAttributes().contains("cbt"));
        product.setPicUrl(melidata.getPicUrl());
        product.setStock(melidata.getAvailableQuantity());
        product.setSales(melidata.getSales());
        product.setRadarLastUpdate(LocalDate.now());

        if (!product.getSellerInfoLastUpdate().isAfter(LocalDate.now().minusDays(SELLER_INFO_LAST_UPDATE_LIMIT_DAYS))){
            setSellerInfo(product, melidata.getMelidata().getEventDataMelidata().getSellerId());
        }

        switch (type){
            case DEFAULT -> updateVisits(product, product.getId(), false);
            case USER_PRODUCT -> updateVisits(product, product.getItemId(), false);
            case CATALOG -> handleCatalogProduct(product, melidata, false);
        }

        productService.saveProduct(product);
        return product;
    }

    //TODO esse metodo eh necessario? ja q estamos pegando o type do SearchItem
    private ProductType getProductType(MelidataResponse melidata){
        if (melidata.getMelidata().getEventDataMelidata().isCatalog()){
            return ProductType.CATALOG;
        }
        if (melidata.getMelidata().getEventDataMelidata().getId().contains("MLBU")){
            return ProductType.USER_PRODUCT;
        }
        return ProductType.DEFAULT;
    }

    private void handleDefaultProduct(Product product, MelidataResponse melidata){
        product.setItemId(product.getId());

        updateVisits(product, product.getId(), false);

        var createdAt = OffsetDateTime.parse(melidata.getGtmEvent().getStartTime()).toLocalDate();
        product.setCreatedAt(createdAt);
    }

    private void handleUserProduct(Product product, MelidataResponse melidata){
        var itemId = melidata.getGtmEvent().getItemId();
        product.setItemId(itemId);

        var itemMelidataUrl = MLUrlBuilder.buildProductInfoUrl(buildItemId(itemId, true));
        var itemMelidata = scraperUtil.getProductDetail(itemMelidataUrl);

        updateVisits(product, itemId, false);

        var createdAt = OffsetDateTime.parse(itemMelidata.getGtmEvent().getStartTime()).toLocalDate();
        product.setCreatedAt(createdAt);
    }

    private void handleCatalogProduct(Product product, MelidataResponse melidata, boolean updateCreatedAt){
        var itemId = melidata.getGtmEvent().getItemId();
        product.setItemId(itemId);

        updateVisits(product, itemId, false);

        if (updateCreatedAt){
            var mlCatalog = mercadoLivreService.getCatalogById(product.getId());
            product.setCreatedAt(mlCatalog.getDateCreated());
        }

    }

    private void setSellerInfo(Product product, String sellerId){
        var seller = mercadoLivreService.getSellerInfo(sellerId);

        product.setSellerId(sellerId);
        product.setSellerNickname(seller.getNickname());
        product.setSellerStoreUrl(seller.getPermalink());
        product.setState(seller.getAddress().getState().replaceFirst("^BR-", ""));
        product.setCity(seller.getAddress().getCity());
        product.setMedal(seller.getSellerReputation().getPowerSellerStatus());
        product.setSellerLevel(seller.getSellerReputation().getLevelId());
        product.setSellerInfoLastUpdate(LocalDate.now());
    }

    private Product checkProductVisits(Product product){
        if (!product.getVisitsLastUpdate().isAfter(LocalDate.now().minusDays(PRODUCT_VISITS_LAST_UPDATE_LIMIT_DAYS))){
            switch (product.getType()){
                case DEFAULT -> updateVisits(product, product.getId(), true);
                case USER_PRODUCT, CATALOG -> updateVisits(product, product.getItemId(), true);
            }
        }
        return product;
    }

    private void updateVisits(Product product, String itemId, boolean shouldSave){
        var visits = mercadoLivreService.getTotalVisitsById(buildItemId(itemId, false));
        product.setVisits(visits);
        product.setVisitsLastUpdate(LocalDate.now());
        if (shouldSave) productService.saveProduct(product);
    }

    private String buildItemId(String itemId, boolean addHifen){
        if (addHifen){
            return itemId.startsWith("MLB-")
                    ? itemId
                    : itemId.replaceFirst("^MLB", "MLB-");
        }else {
            return itemId.startsWith("MLB-")
                    ? itemId.replaceFirst("^MLB-", "MLB")
                    : itemId;
        }
    }

}
