package com.xrervip.super_ai_service.entity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-03-02 13:03
 */

@Data
public class ModelPo {

    public ModelPo(String modelName,String inputClassName,String outputClassName,String modelEngine,byte[] bytes){
        this.modelName = modelName;
        this.inputClassName = inputClassName;
        this.outputClassName = outputClassName;
        this.modelEngine = modelEngine;
        this.modelBlob = bytes;

    }
    private Integer modelId;

    private String modelName;

    private byte[] modelBlob;

    private String inputClassName;

    private String outputClassName;

    private String modelEngine;

}
