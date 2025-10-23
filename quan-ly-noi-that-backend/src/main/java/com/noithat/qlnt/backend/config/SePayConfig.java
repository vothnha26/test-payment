package com.noithat.qlnt.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sepay")
@Data
public class SePayConfig {
    private Api api;
    private String accountNumber;
    private String accountName;
    private String bin;
    private Webhook webhook;
    private Qr qr;

    @Data
    public static class Api {
        private String url;
        private String key;
    }

    @Data
    public static class Webhook {
        private String secret;
    }

    @Data
    public static class Qr {
        private Long timeout;
    }
}
