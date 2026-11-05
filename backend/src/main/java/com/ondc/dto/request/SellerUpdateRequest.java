package com.ondc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequest {
    private String businessName;
    private String ownerName;
    private String phone;
    private String address;
    private String gstNumber;
    private String panNumber;
}

