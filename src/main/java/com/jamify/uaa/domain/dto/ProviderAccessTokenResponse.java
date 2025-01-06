package com.jamify.uaa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProviderAccessTokenResponse {
    @JsonProperty("access_token")
//    @JsonAlias("id_token") for other providers if needed...
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;

}
