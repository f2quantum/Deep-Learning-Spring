package com.xrervip.super_ai_service.rabbitmq;

import java.io.IOException;

import com.xrervip.super_ai_service.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class MQReceiver {

    private final ImageService imageService;

    @RabbitListener(queues=MQConfig.queueName)
    public void receiveMessage(OCRMessage message) throws IOException {
        log.info("MQReceiver-- Receive message session:{}",message.sessionID);
        imageService.handleOcrJob(message);
    }


}