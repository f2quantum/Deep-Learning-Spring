package com.xrervip.super_ai_service.service;

import com.xrervip.super_ai_service.entity.DetectObjectDTO;
import com.xrervip.super_ai_service.rabbitmq.OCRMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 图片服务
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 16:17
 */
public interface ImageService {


    /**
     * 生产者向 mq里提交OCR任务
     * @param sessionID sessionID
     * @param inputStream image 输入流
     * @throws IOException IOException
     */
    void sendOcrJob(String sessionID,InputStream inputStream) throws IOException;

    /**
     * 消费者从mq里获取OCR任务
     * @param ocrMessage ocr任务
     * @throws IOException
     */
    void handleOcrJob(OCRMessage ocrMessage) throws IOException;

    /**
     * 根据sessionID获取ocr 结果
     * @param sessionID sessionID
     * @return dto
     */
    public List<DetectObjectDTO> getOcrResult(String sessionID);
}
