package com.flawlessyou.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "davwgirjs");
        config.put("api_key", "938794558759532");
        config.put("api_secret", "oMGHmTrzBGKclcagAIHF_WxjK_I");
        return new Cloudinary(config);
    }
}