package com.jamify.uaa.constants;

import lombok.Getter;

@Getter
public enum AllowedProviders {
    SPOTIFY("spotify"), AMAZON("amazon"), APPLE("apple");

    private final String value;

    AllowedProviders(String value) {
        this.value = value;
    }
}
