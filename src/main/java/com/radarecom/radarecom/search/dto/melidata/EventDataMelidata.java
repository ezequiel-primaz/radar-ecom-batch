package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventDataMelidata {

    @JsonProperty("seller_id")
    private String sellerId;

    @JsonProperty("official_store_id")
    private String officialStoreId;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("original_price")
    private Double originalPrice;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("catalog_product_id")
    private String catalogProductId;

    @JsonProperty("user_product_id")
    private String userProductId;

    @JsonProperty("listing_type_id")
    private String listingTypeId;

    @JsonProperty("logistic_type")
    private String logisticType;

    @JsonProperty("free_shipping")
    private Boolean freeShipping;

    @JsonProperty("item_attributes")
    private String itemAttributes;

    public String getId(){
        if (isCatalog()) return catalogProductId;
        if (userProductId != null) return userProductId;
        return itemId;
    }

    public boolean isCatalog(){
        return catalogProductId != null && userProductId.equals("not_apply");
    }

}
