package com.skypro.shelterbot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class CacheConfig {
//    @Bean
//    CacheManager cacheManager() {
//        SimpleCacheManager manager = new SimpleCacheManager();
//        manager.setCaches(Arrays.asList(
//                new ConcurrentMapCache("users")
//        ));
//        return manager;
//    }
}
