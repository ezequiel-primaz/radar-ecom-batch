package com.radarecom.radarecom.integration.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MLOauthTokenRequest {

    private String grant_type;
    private String client_id;
    private String client_secret;
    private String refresh_token;

    private String code;
    private String redirect_uri;

}
