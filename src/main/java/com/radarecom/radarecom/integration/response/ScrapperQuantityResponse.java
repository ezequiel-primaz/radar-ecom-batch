package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapperQuantityResponse {

    @JsonProperty("availableQuantity")
    private Integer availableQuantity;

}
