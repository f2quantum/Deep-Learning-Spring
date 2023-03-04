package com.xrervip.super_ai_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-01-27 14:27
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    @GetMapping("hello")
    @Tag(name = "hello", description = "get demo hello world")
    public String hello(){
        log.info("hello");
        return "Hello";
    }
}
