package com.rupiksha.fingpayaeps.faeps.controller;

import com.rupiksha.fingpayaeps.faeps.dto.OnboardRequestDTO;
import com.rupiksha.fingpayaeps.faeps.dto.ResendOtpDTO;
import com.rupiksha.fingpayaeps.faeps.service.OnboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aeps")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // ⚠️ production me restrict karna
public class AepsController {

    private final OnboardService onboardService;

    @PostMapping("/onboard")
    public String onboard(@RequestBody OnboardRequestDTO dto) {

        if (dto == null || dto.getMerchant() == null) {
            throw new RuntimeException("Invalid request payload");
        }

        log.info("🚀 Onboarding request received for merchant: {}",
                dto.getMerchant().getMerchantLoginId());

        return onboardService.onboard(dto);
    }

}