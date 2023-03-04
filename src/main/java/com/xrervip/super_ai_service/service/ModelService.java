package com.xrervip.super_ai_service.service;

import ai.djl.repository.zoo.ZooModel;
import com.xrervip.super_ai_service.entity.ModelDto;
import com.xrervip.super_ai_service.entity.ModelPo;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-25 10:50
 */
public interface ModelService {


    public void addModel(ModelDto modelDto,byte[] bytes);
    /**
     * 将模型加载到内存中
     * @param modelName
     */
    public void loadModel(String modelName);

    public ZooModel getModel(String modelName);


    public byte[] getModelFile(String modelName);
    /**
     * Return true if model is loaded
     * @param modelName
     * @return
     */
    public boolean checkModel(String modelName);

}
