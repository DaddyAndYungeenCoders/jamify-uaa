package com.jamify.uaa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling JWK (JSON Web Key) requests.
 */
@Slf4j
@RestController
public class JwksController {

    @Value("${security.jwt.jwk-key-id}")
    private String keyId;

    /**
     * Endpoint to retrieve the JSON Web Key Set (JWKS).
     *
     * @param request the HttpServletRequest object to get request details
     * @return a map containing the JWKS with a single RSA key.
     */
    @Operation(summary = "Get JSON Web Key Set (JWKS)",
            description = "Endpoint to retrieve the JSON Web Key Set (JWKS).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "JWKS retrieved successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    @GetMapping("/api/v1/oauth/.well-known/jwks.json")
    public Map<String, Object> getJwks(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();
        String remoteUser = request.getRemoteUser();

        log.info("Retrieving JWKS from {}, {}, {}, {}", remoteAddr, remoteHost, remotePort, remoteUser);

        return Map.of(
                "keys", List.of(
                        Map.of(
                                "kty", "RSA", // Key type
                                "alg", "RS256", // Algorithm
                                "use", "sig", // Key use
                                "kid", keyId,
                                "n", "oeGB4cUuLCz-4qf8fPbWEXEy_34nwJZVUdj1pqhKpW-t-RvpkM6YQBKWZ0KPzZMthwMKdqH1pBK6TD-8Reup1vc-kPms5Fjom39nr2_FoFmO0lJzhdq7Pgz0CByluoq6gObNOaXbs0ZxRB7RcBfOpdDqUzAztq011rglOVE_DhrUS68-gkFSAh8wURZEU2vOKAB3k22VqIWTHtdBXuHyPbO0xlAEZUQe6lfNwSYGwv38b2HytAPlsLD_ISBPXd2OhWgBek3e_LTskfHgZusmiVo-_leHNI-njHzfeDsJZVx9rBlHJMu-BIwS5_wWTM4-yrx7onT26Jb8mblWh1ZNCw", // Modulus
                                "e", "AQAB" // Exponent
                        )
                )
        );
    }
}