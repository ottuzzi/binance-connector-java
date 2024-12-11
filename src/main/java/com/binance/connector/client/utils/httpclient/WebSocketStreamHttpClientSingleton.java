package com.binance.connector.client.utils.httpclient;

import java.time.Duration;

import okhttp3.OkHttpClient;

public final class WebSocketStreamHttpClientSingleton {
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().pingInterval(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10)).connectTimeout(Duration.ofSeconds(10)).build();

    private WebSocketStreamHttpClientSingleton() {
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

}
