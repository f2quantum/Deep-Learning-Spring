package com.xrervip.super_ai_service.controller;

import com.xrervip.super_ai_service.constant.ResponseResult;
import com.xrervip.super_ai_service.service.ImageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import com.xrervip.super_ai_service.entity.DetectObjectDTO;
import java.io.IOException;
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


    @PostMapping(path= "/sendOcr",consumes = "multipart/form-data")
    @Tag(name = "sendOCR", description = "识别图片中的文字并返回OCR任务ID")
    public ResponseResult<String>  ocr(HttpServletRequest request
                                                        , @Parameter(
                                                                description = "Image file to be uploaded and ocr.",
                                                                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                                        )@NotNull @RequestPart MultipartFile image) throws  IOException {
        HttpSession session =  request.getSession();
        String sessionID = session.getId();
        imageService.sendOcrJob(sessionID,image.getInputStream());
        return ResponseResult.success(session.getId());
    }

    @GetMapping(path= "/result")
    @Tag(name = "result", description = "获取对应的结果")
    public ResponseResult<List<DetectObjectDTO>>  result(@Parameter(
                                                            description = "Session ID.",allowEmptyValue = false
                                                        ) String sessionID) throws  IOException {
        List<DetectObjectDTO> list = imageService.getOcrResult(sessionID);
        return ResponseResult.success(list);
    }


}
