package com.ondc.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RegexUtil {

    private RegexUtil() {}

    // Indian GSTIN pattern: 2 digits (state code) + 5 chars (PAN) + 4 digits (PAN) + 1 char (PAN) + 1 char + Z + 1 char/digit
    private static final Pattern GSTIN_PATTERN =
            Pattern.compile("\\b(\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1})\\b");

    // Indian Income Tax PAN pattern: 5 uppercase letters + 4 digits + 1 uppercase letter
    private static final Pattern PAN_PATTERN =
            Pattern.compile("\\b([A-Z]{5}\\d{4}[A-Z]{1})\\b");

    // Phone pattern (10 digits starting with 6, 7, 8, or 9)
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\b([6-9]\\d{9})\\b");

    // Business Name indicators
    private static final Pattern[] BUSINESS_NAME_PATTERNS = new Pattern[]{
            Pattern.compile("(?i)(?:legal\\s+name|trade\\s+name|name\\s+of\\s+business|taxpayer\\s+name)\\s*[:\\-]?\\s*([A-Za-z0-9&.,'\\-\\s]{3,60})"),
            Pattern.compile("(?i)(?:m/s|messrs)\\.?\\s+([A-Za-z0-9&.,'\\-\\s]{3,60})")
    };

    // Address indicators
    private static final Pattern ADDRESS_PATTERN =
            Pattern.compile("(?i)(?:principal\\s+place\\s+of\\s+business|registered\\s+office|address)\\s*[:\\-]?\\s*([A-Za-z0-9#&.,/\\-\\s\\n]{10,150})");

    public static String extractGstNumber(String text) {
        if (text == null) return null;
        Matcher matcher = GSTIN_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        return null;
    }

    public static String extractPanNumber(String text) {
        if (text == null) return null;
        // First look for explicit PAN
        Matcher panMatcher = PAN_PATTERN.matcher(text);
        if (panMatcher.find()) {
            return panMatcher.group(1).toUpperCase();
        }
        // If GST is present, PAN is characters 3 to 13 of GSTIN
        String gst = extractGstNumber(text);
        if (gst != null && gst.length() == 15) {
            return gst.substring(2, 12).toUpperCase();
        }
        return null;
    }

    public static String extractPhone(String text) {
        if (text == null) return null;
        Matcher matcher = PHONE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractBusinessName(String text) {
        if (text == null) return null;
        for (Pattern pattern : BUSINESS_NAME_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String candidate = cleanExtractedString(matcher.group(1));
                if (candidate != null && candidate.length() >= 3) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public static String extractAddress(String text) {
        if (text == null) return null;
        Matcher matcher = ADDRESS_PATTERN.matcher(text);
        if (matcher.find()) {
            return cleanExtractedString(matcher.group(1));
        }
        return null;
    }

    public static Map<String, String> extractAllFields(String text) {
        Map<String, String> fields = new LinkedHashMap<>();
        if (text == null || text.isBlank()) {
            return fields;
        }

        String gst = extractGstNumber(text);
        if (gst != null) fields.put("gstNumber", gst);

        String pan = extractPanNumber(text);
        if (pan != null) fields.put("panNumber", pan);

        String businessName = extractBusinessName(text);
        if (businessName != null) fields.put("businessName", businessName);

        String address = extractAddress(text);
        if (address != null) fields.put("address", address);

        String phone = extractPhone(text);
        if (phone != null) fields.put("phone", phone);

        return fields;
    }

    private static String cleanExtractedString(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replaceAll("[\\r\\n]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
        // Cut off if hitting subsequent label keyword
        int cutoff = cleaned.toLowerCase().indexOf("gstin");
        if (cutoff > 0) cleaned = cleaned.substring(0, cutoff).trim();
        cutoff = cleaned.toLowerCase().indexOf("date of");
        if (cutoff > 0) cleaned = cleaned.substring(0, cutoff).trim();
        return cleaned;
    }
}
