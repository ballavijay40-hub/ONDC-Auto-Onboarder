package com.ondc;

import com.ondc.util.RegexUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexUtilTest {

    @Test
    void testExtractGstNumber() {
        String sampleGst = "Government of India GST Certificate\nLegal Name: ABC TRADERS\nGSTIN: 29ABCDE1234A1Z5\nDate: 01/01/2024";
        String extracted = RegexUtil.extractGstNumber(sampleGst);
        assertEquals("29ABCDE1234A1Z5", extracted);
    }

    @Test
    void testExtractPanFromGst() {
        String sampleGst = "GSTIN: 29ABCDE1234A1Z5";
        String pan = RegexUtil.extractPanNumber(sampleGst);
        assertEquals("ABCDE1234A", pan);
    }

    @Test
    void testExtractAllFields() {
        String text = "Taxpayer Name: ABC Fashion Studio\nGSTIN: 29ABCDE1234A1Z5\nAddress: Flat 101, MG Road, Bangalore\nPhone: 9876543210";
        Map<String, String> fields = RegexUtil.extractAllFields(text);

        assertEquals("29ABCDE1234A1Z5", fields.get("gstNumber"));
        assertEquals("ABCDE1234A", fields.get("panNumber"));
        assertNotNull(fields.get("businessName"));
        assertEquals("9876543210", fields.get("phone"));
    }
}
