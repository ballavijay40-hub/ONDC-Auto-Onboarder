package com.ondc.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "compliance")
public class ComplianceRecord {

    public static final String READY = "ONDC Ready";
    public static final String NEEDS_ATTENTION = "Needs Attention";

    @Id
    private String id;

    @Indexed
    private String sellerId;

    private int score;
    private String status;
    private List<String> remarks = new ArrayList<>();
    private List<String> missingItems = new ArrayList<>();
    private Instant checkedAt;
}
