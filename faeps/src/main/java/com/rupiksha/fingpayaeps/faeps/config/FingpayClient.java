package com.rupiksha.fingpayaeps.faeps.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class FingpayClient {

    private final RestTemplate rest;

    // 🔥 CHANGE: body → Object
    public String post(String url, Object body, HttpHeaders headers){

        try {

            // ❌ REMOVE THIS (important)
            // headers.setContentType(MediaType.TEXT_PLAIN);

            // ✅ Accept JSON response
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // ✅ Let caller decide Content-Type (DO NOT OVERRIDE)

            HttpEntity<Object> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> res =
                    rest.exchange(url, HttpMethod.POST, entity, String.class);

            log.info("Fingpay Raw Response: {}", res.getBody());

            return res.getBody();

        } catch (Exception e) {

            log.error("Fingpay API call failed", e);

            throw new RuntimeException("Fingpay API Error", e);
        }
    }
}