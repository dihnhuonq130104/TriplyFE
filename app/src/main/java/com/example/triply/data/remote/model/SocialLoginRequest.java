package com.example.triply.data.remote.model;

public class SocialLoginRequest {
    private String provider;
    private String token;

    public SocialLoginRequest(String provider, String token) {
        this.provider = provider;
        this.token = token;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


