package com.el_jumillano.pac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean("ioTaskExecutor")
    public Executor ioTaskExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}
