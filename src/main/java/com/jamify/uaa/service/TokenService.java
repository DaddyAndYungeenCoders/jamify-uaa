package com.jamify.uaa.service;

import java.util.Map;

public interface TokenService {
    Map<String, String> refreshAccessToken(String provider, String email);
}
