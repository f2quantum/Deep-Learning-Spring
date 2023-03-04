package com.xrervip.super_ai_service.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 16:17
 */
public interface ImageService {

    DetectedObjects ocr(InputStream inputStream);
}
