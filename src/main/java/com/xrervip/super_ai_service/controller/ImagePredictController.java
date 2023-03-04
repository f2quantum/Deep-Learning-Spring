package com.xrervip.super_ai_service.controller;

import com.xrervip.super_ai_service.constant.ResponseResult;
import com.xrervip.super_ai_service.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.xrervip.super_ai_service.entity.DetectObjectDto;
import java.io.IOException;
import java.util.stream.Collectors;
import ai.djl.modality.cv.output.DetectedObjects;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-25 11:15
 */
@RestController
@RequestMapping("/image")
@Slf4j
@AllArgsConstructor
public class ImagePredictController {


    private final ImageService imageService;
    @GetMapping("predict")
    @Tag(name = "predict", description = "predict image from file")
    public String predict(@RequestPart MultipartFile file){
        log.info("hello");
        return "Hello";
    }


    @PostMapping("/ocr")
    @Tag(name = "ocr", description = "识别图片中的文字并返回结果")
    public ResponseResult<List<DetectObjectDto>>  ocr(@RequestPart MultipartFile image) throws  IOException {

        DetectedObjects result = imageService.ocr(image.getInputStream());

        List<DetectObjectDto> ret = result.items().stream().map(DetectObjectDto::new).collect(Collectors.toList());
        return ResponseResult.success(ret);
    }



}
