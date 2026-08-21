package com.shopsphere.adminservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private String userEmail;
    private Double totalAmount;
    private String status;
    private String address;
    private String paymentMode;
    private LocalDateTime createdAt;
}