package com.jamify.uaa.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "User token details")
public class UserToken {
    @Schema(description = "User email", example = "user@example.com")
    private String email;
    @Schema(description = "Provider name", example = "spotify")
    private String provider;
    @Schema(description = "Access token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgjueVfjZVZL4OzygP0c5b2f")
    private String accessToken;
    @Schema(description = "Expiration time of the access token", example = "2021-08-01T12:00:00")
    private LocalDateTime expiresAt;
}

