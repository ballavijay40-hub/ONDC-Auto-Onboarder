package com.ondc.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceResponse {
    private String id;
    private String sellerId;
    private int score;
    private String status;

    @Builder.Default
    private List<String> remarks = new ArrayList<>();

    @Builder.Default
    private List<String> missingItems = new ArrayList<>();

    @Builder.Default
    private List<String> passedChecks = new ArrayList<>();

    private String providerUsed;
    private Instant checkedAt;
}
