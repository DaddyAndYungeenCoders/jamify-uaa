package com.jamify.uaa.constants;

import lombok.Getter;

@Getter
public enum AllowedProviders {
    SPOTIFY("spotify"), DEEZER("deezer"), APPLE_MUSIC("apple_music");

    private final String value;

    AllowedProviders(String value) {
        this.value = value;
    }
}
