package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLUserSellerReputationResponse {

    @JsonProperty("level_id")
    private String levelId;

    @JsonProperty("power_seller_status")
    private String powerSellerStatus;

}
