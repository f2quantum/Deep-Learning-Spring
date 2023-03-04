package com.xrervip.super_ai_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-03-03 20:38
 */
@Data
@Schema
public class ModelDto {

    private String modelName;

    private String inputClassName;

    private String outputClassName;

    private String modelEngine;

}
