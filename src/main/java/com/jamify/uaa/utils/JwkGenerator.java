package com.jamify.uaa.utils;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JwkGenerator {
    @Value("${security.jwt.jwk-key-id}")
    private static String keyId;

    public static void main(String[] args) throws Exception {
        String publicKeyPEM = new String(Files.readAllBytes(Paths.get("public.pem")))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyFactory.generatePublic(spec))
                .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                .algorithm(new com.nimbusds.jose.Algorithm("RS256"))
                .keyID(keyId)
                .build();

        System.out.println(rsaKey.toJSONObject());
    }
}
