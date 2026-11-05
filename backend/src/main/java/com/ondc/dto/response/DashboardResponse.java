package com.ondc.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private SellerResponse seller;
    private long totalDocuments;
    private long verifiedDocuments;
    private long totalProducts;
    private long aiGeneratedProducts;
    private int complianceScore;
    private String complianceStatus;
    private boolean ondcReady;
    private List<String> pendingActionItems;
    private Map<String, Boolean> onboardingFunnel;
}
