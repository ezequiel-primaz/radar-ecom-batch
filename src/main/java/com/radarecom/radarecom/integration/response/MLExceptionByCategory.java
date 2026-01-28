package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLExceptionByCategory {

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("configuration")
    private MLExceptionByCategoryConfiguration configuration;

}
