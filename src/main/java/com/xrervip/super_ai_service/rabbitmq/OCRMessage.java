package com.xrervip.super_ai_service.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRMessage implements Serializable {

    public String sessionID;

    public byte[] imageInBytes;

}
