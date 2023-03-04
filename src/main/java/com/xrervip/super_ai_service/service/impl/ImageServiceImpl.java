package com.xrervip.super_ai_service.service.impl;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;
import com.xrervip.super_ai_service.common.ImageProperties;
import com.xrervip.super_ai_service.service.ImageService;
import com.xrervip.super_ai_service.service.ModelService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.xrervip.super_ai_service.common.ImageUtils.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 16:17
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ModelService modelService;

    private final ImageProperties properties;

    @SneakyThrows
    public DetectedObjects ocr(InputStream inputStream)  {
        Image image = ImageFactory.getInstance().fromInputStream(inputStream);

        DetectedObjects detect = detect(image);

        List<String> names = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        List<BoundingBox> rect = new ArrayList<>();

        List<DetectedObjects.DetectedObject> list = detect.items();
        for (DetectedObjects.DetectedObject result : list) {
            BoundingBox box = result.getBoundingBox();

            //扩展文字块的大小，因为检测出来的文字块比实际文字块要小
            Image subImg = getSubImage(image, extendBox(box));

            if (subImg.getHeight() * 1.0 / subImg.getWidth() > 1.5) {
                subImg = rotateImage(subImg);
            }

            Classifications.Classification classifications = checkRotate(subImg);
            if ("Rotate".equals(classifications.getClassName()) && classifications.getProbability() > properties.getRotateThreshold()) {
                subImg = rotateImage(subImg);
            }

            String name = recognize(subImg);
            names.add(name);
            prob.add(1.0);
            rect.add(box);
        }

        return new DetectedObjects(names, prob, rect);
    }

    /**
     * 文字识别
     *
     * @param image   图片
     */
    @SneakyThrows
    private String recognize(Image image) {

        ZooModel<Image, String> model= modelService.getModel("recognitionModel");
        Predictor<Image, String> recognizer = model.newPredictor();

        return recognizer.predict(image);

    }


    /**
     * 判断文字角度，如果需要旋转则进行相应处理
     *
     * @return Classifications
     */
    @SneakyThrows
    private Classifications.Classification checkRotate(Image image) {

        ZooModel<Image, Classifications> model= modelService.getModel("rotateModel");

        try (Predictor<Image, Classifications> rotateClassifier = model.newPredictor()) {
            Classifications predict = rotateClassifier.predict(image);
            log.debug("word rotate: {}", predict);
            return predict.best();
        }
    }


    /**
     * 检测文字所在区域
     *
     * @param image   图片
     * @return 检测结果
     */
    @SneakyThrows
    public DetectedObjects detect(Image image) {

        long startTime = System.currentTimeMillis();

        DetectedObjects result;
        //检测文字所在区域

        ZooModel<Image, DetectedObjects> model= modelService.getModel("detectionModel");

        Predictor<Image, DetectedObjects> detector = model.newPredictor() ;
        result = detector.predict(image);


        long endTime = System.currentTimeMillis();
        log.debug("检测时长{}mm, 结果：{}", (endTime - startTime), result);

        return result;
    }


}
