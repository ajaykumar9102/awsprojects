package com.rupiksha.fingpayaeps.faeps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class AadhaarValidator {

    private static final Logger log = LoggerFactory.getLogger(AadhaarValidator.class);

    // 🔥 Precompiled regex (performance optimized)
    private static final Pattern NON_DIGIT = Pattern.compile("\\D");
    private static final Pattern SAME_DIGITS = Pattern.compile("(\\d)\\1{11}");

    // 🔐 Verhoeff tables
    private static final int[][] d = {
            {0,1,2,3,4,5,6,7,8,9},
            {1,2,3,4,0,6,7,8,9,5},
            {2,3,4,0,1,7,8,9,5,6},
            {3,4,0,1,2,8,9,5,6,7},
            {4,0,1,2,3,9,5,6,7,8},
            {5,9,8,7,6,0,4,3,2,1},
            {6,5,9,8,7,1,0,4,3,2},
            {7,6,5,9,8,2,1,0,4,3},
            {8,7,6,5,9,3,2,1,0,4},
            {9,8,7,6,5,4,3,2,1,0}
    };

    private static final int[][] p = {
            {0,1,2,3,4,5,6,7,8,9},
            {1,5,7,6,2,8,3,0,9,4},
            {5,8,0,3,7,9,6,1,4,2},
            {8,9,1,6,0,4,3,5,2,7},
            {9,4,5,3,1,2,6,8,7,0},
            {4,2,8,6,5,7,3,9,0,1},
            {2,7,9,3,8,0,6,4,1,5},
            {7,0,4,6,9,1,3,2,5,8}
    };

    private AadhaarValidator() {
        // prevent instantiation
    }

    /**
     * ✅ Aadhaar validation (safe for AEPS)
     * @param aadhaar Aadhaar number
     * @param strict enable Verhoeff check
     */
    public static boolean isValid(String aadhaar, boolean strict) {

        if (aadhaar == null || aadhaar.trim().isEmpty()) {
            log.warn("Aadhaar validation failed: empty input");
            return false;
        }

        // 🔹 Remove non-digits
        aadhaar = NON_DIGIT.matcher(aadhaar).replaceAll("");

        // 🔹 Length check
        if (aadhaar.length() != 12) {
            log.warn("Aadhaar validation failed: invalid length");
            return false;
        }

        // 🔹 Reject all same digits
        if (SAME_DIGITS.matcher(aadhaar).matches()) {
            log.warn("Aadhaar validation failed: repeated digits");
            return false;
        }

        // 🔐 Optional strict validation (Verhoeff)
        if (strict && !validateVerhoeff(aadhaar)) {
            log.warn("Aadhaar validation failed: checksum invalid");
            return false;
        }

        return true;
    }

    /**
     * Default validation (non-strict for AEPS)
     */
    public static boolean isValid(String aadhaar) {
        return isValid(aadhaar, false);
    }

    // 🔐 Verhoeff algorithm
    private static boolean validateVerhoeff(String num) {
        int c = 0;

        for (int i = 0; i < num.length(); i++) {
            int digit = num.charAt(num.length() - i - 1) - '0';
            c = d[c][p[i % 8][digit]];
        }

        return c == 0;
    }

    // 🔐 Mask Aadhaar for safe logging (if needed)
    public static String mask(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) return "****";
        return aadhaar.replaceAll("\\d(?=\\d{4})", "*");
    }
}