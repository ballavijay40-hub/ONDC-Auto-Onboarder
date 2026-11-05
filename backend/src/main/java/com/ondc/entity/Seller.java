package com.ondc.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "sellers")
public class Seller {

    public static final String ROLE_SELLER = "SELLER";

    @Id
    private String id;

    private String businessName;
    private String ownerName;

    @Indexed(unique = true)
    private String email;

    private String phone;
    private String passwordHash;
    private String gstNumber;
    private String panNumber;
    private String address;
    private String role = ROLE_SELLER;
    private Instant createdAt;
}
