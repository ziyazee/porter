package com.porter.porter.booking.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TrackingTokenGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;
    
    public static String generateTrackingToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    public static String generateBookingNumber() {
        int year = java.time.Year.now().getValue();
        long randomPart = (long) (secureRandom.nextDouble() * 10000);
        return "CG-" + year + "-" + randomPart;
    }
    
    public static String generateTrackingUrl(String trackingToken) {
        return "https://cargogo.sa/t/" + trackingToken;
    }
}
