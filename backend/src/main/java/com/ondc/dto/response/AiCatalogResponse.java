package com.ondc.dto.response;

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
public class AiCatalogResponse {
    private String title;
    private String description;
    private String category;
    private String brand;
    private String color;
    private String size;
    private String gender;
    private String material;
    private String careInstructions;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<String> highlights = new ArrayList<>();

    private String providerUsed;
    private boolean fallbackUsed;
}

