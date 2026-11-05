package com.ondc.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {
    private String id;
    private String businessName;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String gstNumber;
    private String panNumber;
    private String role;
    private Instant createdAt;
}

