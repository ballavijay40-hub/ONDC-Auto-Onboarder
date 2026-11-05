package com.ondc.dto.response;

import java.time.Instant;
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
public class OndcBecknCatalogResponse {
    private String schemaVersion;
    private String domain;
    private Instant exportedAt;
    private Map<String, Object> provider;
    private List<Map<String, Object>> items;
    private Map<String, Object> complianceSummary;
}
