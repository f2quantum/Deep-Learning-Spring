package com.xrervip.super_ai_service.service.impl;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.xrervip.super_ai_service.common.ImageProperties;
import com.xrervip.super_ai_service.entity.DetectObjectDTO;
import com.xrervip.super_ai_service.exception.ResourceNotFoundException;
import com.xrervip.super_ai_service.rabbitmq.MQSender;
import com.xrervip.super_ai_service.rabbitmq.OCRMessage;
import com.xrervip.super_ai_service.redis.ImageKey;
import com.xrervip.super_ai_service.redis.RedisService;
import com.xrervip.super_ai_service.service.ImageService;
import com.xrervip.super_ai_service.service.ModelService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import static com.xrervip.super_ai_service.common.ImageUtils.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 图片服务的实现类
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 16:17
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final RedisService redisService;

    private final ModelService modelService;

    private final ImageProperties properties;

    private final MQSender sender;

    private final Set<String> sessions = new ConcurrentSkipListSet<>();
    @Override
    public void sendOcrJob(String sessionID,InputStream inputStream) throws IOException {
        OCRMessage message = new OCRMessage(sessionID,inputStream.readAllBytes());
        sender.sendTopic(message);
        sessions.add(sessionID);
    }


    @Override
    public void handleOcrJob(OCRMessage message) throws IOException {
        Image image = ImageFactory.getInstance().fromInputStream(new ByteArrayInputStream(message.getImageInBytes()));
        DetectedObjects objects = ocr(image);
        Set<DetectObjectDTO> result = objects.items().stream().map(DetectObjectDTO::new).collect(Collectors.toSet());
        redisService.addSet(ImageKey.imageKey,message.sessionID,result);
    }


    @Override
    public List<DetectObjectDTO> getOcrResult(String sessionID){

        Set<DetectObjectDTO> result = redisService.getSet(ImageKey.imageKey,sessionID);
        if(result!=null&&result.size()>0){
            return result.stream().toList();
        }else if(sessions.contains(sessionID)) {
            return new ArrayList<>();
        }else{
            throw new ResourceNotFoundException("SessionID not exist");
        }

    }
    @SneakyThrows
    private DetectedObjects ocr(Image image)  {

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

        ZooModel<Image, String> model= modelService.getOrLoadModel("recognize");
        Predictor<Image, String> recognizer = model.newPredictor();
        Object result = recognizer.predict(image);
        log.info("识别结果: {}", result);
        return result.toString();

    }


    /**
     * 判断文字角度，如果需要旋转则进行相应处理
     *
     * @return Classifications
     */
    @SneakyThrows
    private Classifications.Classification checkRotate(Image image) {

        ZooModel<Image, Classifications> model= modelService.getOrLoadModel("rotate");

        Predictor<Image, Classifications> rotateClassifier = model.newPredictor();
        Classifications predict = rotateClassifier.predict(image);
        log.info("word rotate: {}", predict);
        return predict.best();

    }


    /**
     * 检测文字所在区域
     *
     * @param image   图片
     * @return 检测结果
     */
    public DetectedObjects detect(Image image) throws TranslateException {

        long startTime = System.currentTimeMillis();

        DetectedObjects result;
        //检测文字所在区域

        ZooModel<Image, DetectedObjects> model= modelService.getOrLoadModel("detect");

        Predictor<Image, DetectedObjects> detector = model.newPredictor() ;
        result = detector.predict(image);


        long endTime = System.currentTimeMillis();
        log.info("检测时长{}mm, 结果：{}", (endTime - startTime), result);

        return result;
    }


}
