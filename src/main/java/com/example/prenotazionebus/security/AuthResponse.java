package com.example.prenotazionebus.security;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    private String msg;

    public  AuthResponse(){}

    public AuthResponse(String msg) {
        this.msg = msg;
    }

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMsg() {
        return msg;
    }

    public AuthResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}