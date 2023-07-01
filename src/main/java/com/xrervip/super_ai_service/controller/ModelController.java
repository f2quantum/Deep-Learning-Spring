package com.xrervip.super_ai_service.controller;

import com.xrervip.super_ai_service.constant.ResponseResult;
import com.xrervip.super_ai_service.entity.ModelInfoDTO;
import com.xrervip.super_ai_service.service.ModelService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-03-03 20:34
 */
@Slf4j
@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
@Validated
public class ModelController {


    private final ModelService modelService;

//    @PostMapping(path = "/add",consumes = "multipart/form-data")
//    @Tag(name = "add model", description = "add model to the server ")
//    public ResponseResult<String> add(@RequestParam(value = "modelDto") String contentParam,
//                                      @Parameter(
//                                              description = "Files to be uploaded",
//                                              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
//                                      )
//                                      @RequestPart  (value = "files", required = true) MultipartFile file) throws IOException {
//        Gson gson = new Gson();
//        @ValidModel ModelDto modelDto = gson.fromJson(contentParam, ModelDto.class);
//
//        log.info("Add or update model : {}",modelDto.getModelName());
//        byte [] byteArr=file.getBytes();
//        modelService.addModel(modelDto,byteArr);
//        return ResponseResult.success("add success");
//    }

    @PostMapping(path = "/add",consumes = {"multipart/form-data"})
    @Tag(name = "add model", description = "add model to the server ")
    public ResponseResult<String> add(ModelInfoDTO modelInfoDto,
                                      @Parameter(
                                              description = "Files to be uploaded",
                                              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                      )
                                      @NotNull @RequestPart  (value = "files", required = true) MultipartFile file) throws IOException {
        log.info("Add model : {}", modelInfoDto.getModelName());
        byte [] byteArr=file.getBytes();
        modelService.addModel(modelInfoDto,byteArr);
        return ResponseResult.success("add success");
    }

    @GetMapping("/get")
    @Tag(name = "get model file", description = "download model from server ")
    public void get(String modelName,HttpServletResponse response) throws IOException{

        byte[] model = modelService.getModelFile(modelName);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=modelFile_" + modelName + ".zip");

        response.getOutputStream().write(model, 0, model.length);

    }

    @PostMapping(path = "/load")
    @Tag(name = "load model", description = "load model to the server ")
    public ResponseResult<String> add(String modelName) throws IOException {
        modelService.loadModel(modelName);
        return ResponseResult.success("load successful !");
    }
}
