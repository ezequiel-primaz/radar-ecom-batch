package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventDataFireBase {

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("catalog_product_id")
    private String catalogProductId;

    @JsonProperty("catalog_product_name")
    private String catalogProductName;

    public String getName(){
        if (itemName != null) return itemName;
        if (catalogProductName != null ) return catalogProductName;
        return null;
    }

}
