package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GtmEvent {

    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("rootCategoryId")
    private String rootCategoryId;

    @JsonProperty("categoryId")
    private String categoryId;

}
