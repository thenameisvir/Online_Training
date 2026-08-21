package com.shopsphere.catalogservice.listener;

import com.shopsphere.catalogservice.config.RabbitMQConfig;
import com.shopsphere.catalogservice.dto.StockUpdateMessage;
import com.shopsphere.catalogservice.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdateListener {

    private final CatalogService catalogService;

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(StockUpdateMessage message) {
        log.info("Stock update message received: productId={}, quantity={}",
                message.getProductId(), message.getQuantity());
        catalogService.updateStock(message.getProductId(), message.getQuantity());
    }
}

