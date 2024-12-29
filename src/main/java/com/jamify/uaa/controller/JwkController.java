package com.jamify.uaa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling JWK (JSON Web Key) requests.
 */
@RestController
public class JwkController {

    /**
     * Endpoint to retrieve the JSON Web Key Set (JWKS).
     *
     * @return a map containing the JWKS with a single RSA key.
     */
    @GetMapping("/oauth/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return Map.of(
                "keys", List.of(
                        Map.of(
                                "kty", "RSA", // Key type
                                "alg", "RS256", // Algorithm
                                "use", "sig", // Key use
                                "kid", "jamify-uaa-key-id", // Key ID
                                "n", "oeGB4cUuLCz-4qf8fPbWEXEy_34nwJZVUdj1pqhKpW-t-RvpkM6YQBKWZ0KPzZMthwMKdqH1pBK6TD-8Reup1vc-kPms5Fjom39nr2_FoFmO0lJzhdq7Pgz0CByluoq6gObNOaXbs0ZxRB7RcBfOpdDqUzAztq011rglOVE_DhrUS68-gkFSAh8wURZEU2vOKAB3k22VqIWTHtdBXuHyPbO0xlAEZUQe6lfNwSYGwv38b2HytAPlsLD_ISBPXd2OhWgBek3e_LTskfHgZusmiVo-_leHNI-njHzfeDsJZVx9rBlHJMu-BIwS5_wWTM4-yrx7onT26Jb8mblWh1ZNCw", // Modulus
                                "e", "AQAB" // Exponent
                        )
                )
        );
    }
}