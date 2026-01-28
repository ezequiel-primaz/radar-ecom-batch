package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MelidataResponse {

    @JsonProperty("melidata")
    private Melidata melidata;

    @JsonProperty("fireBaseEvent")
    private FireBaseEvent fireBaseEvent;

    @JsonProperty("gtmEvent")
    private GtmEvent gtmEvent;

    @JsonProperty("picUrl")
    private String picUrl;

    @JsonProperty("availableQuantity")
    private Integer availableQuantity;

    @JsonProperty("sales")
    private Integer sales;

}
