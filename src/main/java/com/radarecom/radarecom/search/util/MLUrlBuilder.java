package com.radarecom.radarecom.search.util;

import com.radarecom.radarecom.search.filter.SearchFilter;

import static java.util.Objects.nonNull;

public class MLUrlBuilder {

    private static final String ML_SEARCH_URL = "https://lista.mercadolivre.com.br/";
    private static final String ML_ITEM_INFO_URL = "https://produto.mercadolivre.com.br/";

    private static final String FULL = "Frete_Full";
    private static final String FREE_SHIPPING = "CustoFrete_Gratis";
    private static final String ONLY_OFFICIAL_STORE = "Loja_all";
    private static final String PRICE_RANGE = "PriceRange";
    private static final String INTERNATIONAL = "SHIPPING*ORIGIN_10215069";
    private static final String NO_INDEX_TRUE = "NoIndex_True";
    private static final String PAGE = "Desde";

    private static final Integer ML_PAGE_QUANTITY = 48;

    public static String buildSearchByKeywordUrl(String keyword, SearchFilter searchFilter){
        var url = new StringBuilder(ML_SEARCH_URL);
        url.append(keyword);
        url.append("_");

        buildFilters(url, searchFilter);

        return url.toString();
    }

    public static String buildSearchByCategoryUrl(String categoryUrl, SearchFilter searchFilter){
        var url = new StringBuilder(categoryUrl);
        url.append("/\"_");

        buildFilters(url, searchFilter);

        return url.toString();
    }

    public static String buildProductInfoUrl(String itemId){
        return ML_ITEM_INFO_URL + itemId;
    }

    private static void buildFilters(StringBuilder url, SearchFilter searchFilter){
        if (nonNull(searchFilter.getFreeShipping()) && searchFilter.getFreeShipping()){
            url.append(FREE_SHIPPING);
            url.append("_");
        }

        if (searchFilter.isOnlyLogisticTypeFull()){
            url.append(FULL);
            url.append("_");
        }

        if (nonNull(searchFilter.getOnlyOfficialStore()) && searchFilter.getOnlyOfficialStore()){
            url.append(ONLY_OFFICIAL_STORE);
            url.append("_");
        }

        if (nonNull(searchFilter.getMinPrice()) || nonNull(searchFilter.getMaxPrice())){
            var minValue = nonNull(searchFilter.getMinPrice()) ? searchFilter.getMinPrice() : 0;
            var maxValue = nonNull(searchFilter.getMaxPrice()) ? searchFilter.getMaxPrice() : 0;
            url.append(PRICE_RANGE);
            url.append("_");
            url.append(minValue).append("BRL");
            url.append("-");
            url.append(maxValue).append("BRL");
            url.append("_");
        }

        setPage(url, searchFilter.getPage());
        url.append(NO_INDEX_TRUE);

        if (nonNull(searchFilter.getInternational()) && searchFilter.getInternational()){
            url.append("_");
            url.append(INTERNATIONAL);
        }
    }

    private static void setPage(StringBuilder url, Integer page){
        if (!page.equals(0)) {
            url.append(PAGE);
            url.append("_");
            var desde = (page * ML_PAGE_QUANTITY) + 1;
            url.append(desde);
            url.append("_");
        }

    }

}
