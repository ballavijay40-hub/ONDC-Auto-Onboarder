package com.ondc.controller;

import com.ondc.dto.response.DashboardResponse;
import com.ondc.service.DashboardService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getMyStats(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(dashboardService.getDashboardStats(sellerId));
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<DashboardResponse> getStatsBySellerId(@PathVariable String sellerId) {
        return ResponseEntity.ok(dashboardService.getDashboardStats(sellerId));
    }
}

