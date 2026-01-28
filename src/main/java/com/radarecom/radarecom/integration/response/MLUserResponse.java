package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLUserResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("permalink")
    private String permalink;

    @JsonProperty("address")
    private MLUserAddressResponse address;

    @JsonProperty("seller_reputation")
    private MLUserSellerReputationResponse sellerReputation;

}
