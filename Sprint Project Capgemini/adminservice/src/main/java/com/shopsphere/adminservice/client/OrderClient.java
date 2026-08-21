package com.shopsphere.adminservice.client;

import com.shopsphere.adminservice.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "orderservice")
public interface OrderClient {

    @GetMapping("/orders/all")
    List<Object> getAllOrders();

    @PutMapping("/orders/{id}/status")
    OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body);
}