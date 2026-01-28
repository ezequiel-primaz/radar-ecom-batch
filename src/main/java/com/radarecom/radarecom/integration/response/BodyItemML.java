package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BodyItemML {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("subtitle")
    private String subtitle;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("sold_quantity")
    private Integer soldQuantity;

    @JsonProperty("date_created")
    private String createDate;

    @JsonProperty("last_updated")
    private String updateDate;

    @JsonProperty("listing_type_id")
    private String listingTypeId;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("attributes")
    private List<Attribute> attributes;

    @JsonProperty("variations")
    private List<Variations> variations;

}
