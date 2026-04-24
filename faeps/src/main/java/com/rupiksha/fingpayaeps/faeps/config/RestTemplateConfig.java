package com.rupiksha.fingpayaeps.faeps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {

        // ✅ ADD TIMEOUT (IMPORTANT)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(20000);

        RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(factory)
        );

        restTemplate.getInterceptors().add((request, body, execution) -> {

            // 🔹 REQUEST LOG
            log.info("➡️ URL: {}", request.getURI());
            log.info("➡️ METHOD: {}", request.getMethod());

            log.info("➡️ HEADERS:");
            request.getHeaders().forEach((key, value) ->
                    log.info("{} : {}", key, value)
            );

            // 🔥 SAFE BODY LOG (MASKED)
            String bodyStr = new String(body, StandardCharsets.UTF_8);

            // Aadhaar mask
            bodyStr = bodyStr.replaceAll("\\d{12}", "XXXXXXXXXXXX");

            log.info("➡️ BODY: {}", bodyStr);

            // 🔹 EXECUTE
            ClientHttpResponse response = execution.execute(request, body);

            // 🔹 RESPONSE LOG
            String responseBody = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            log.info("⬅️ STATUS: {}", response.getStatusCode());
            log.info("⬅️ RESPONSE BODY: {}", responseBody);

            return response;
        });

        return restTemplate;
    }
}