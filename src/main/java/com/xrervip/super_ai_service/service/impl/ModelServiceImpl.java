package com.xrervip.super_ai_service.service.impl;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.paddlepaddle.zoo.cv.objectdetection.PpWordDetectionTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import com.xrervip.super_ai_service.common.ModelProperties;
import com.xrervip.super_ai_service.dao.ModelMapper;
import com.xrervip.super_ai_service.entity.ModelDto;
import com.xrervip.super_ai_service.entity.ModelPo;
import com.xrervip.super_ai_service.exception.ModelException;
import com.xrervip.super_ai_service.service.ModelService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xrervip.super_ai_service.common.Constants.ENGINE_PADDLE;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 19:56
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelProperties prop;

    private final ModelMapper modelMapper;
    private final Map<String,ZooModel> models = new HashMap<>();

    @Override
    public void addModel(ModelDto modelDto,byte[] bytes) {
        // todo
        ModelPo modelPo = new ModelPo(modelDto.getModelName(),modelDto.getInputClassName(),modelDto.getOutputClassName(),modelDto.getModelEngine(),bytes);
        modelMapper.insert(modelPo);
    }

    @Override
    public void loadModel(String modelName) {
        if(this.checkModel(modelName)) return;
        ModelPo modelPo = modelMapper.selectByModelName(modelName);
        if(modelPo==null) throw new ModelException("Model:"+ modelName+" not exist");

        Device device = Device.Type.CPU.equalsIgnoreCase(prop.getDeviceType()) ? Device.cpu() : Device.gpu();

        try {
            Class inputClass = Class.forName(modelPo.getInputClassName());
            Class outputClass = Class.forName(modelPo.getOutputClassName());

            String engine = modelPo.getModelEngine();
            byte[] modelBlob = modelPo.getModelBlob();

            String filepath =  "jar://models/" + modelName +".zip";
            File file  = new File(filepath);
            if(file.exists()){
                file.delete();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(modelBlob,0,modelBlob.length);
            fos.flush();
            fos.close();


            ZooModel model  =  Criteria.builder()
                    .optEngine(engine)
                    .setTypes(inputClass, outputClass)
                    .optModelUrls(filepath)
                    .optTranslator(new PpWordDetectionTranslator(new ConcurrentHashMap<>()))
                    .optDevice(device)
                    .build()
                    .loadModel();

            models.put(modelName,model);

        } catch (ClassNotFoundException | ModelNotFoundException | MalformedModelException | IOException e) {
            throw new ModelException(e);
        }



    }

    @Override
    public ZooModel getModel(String modelName) {
        return this.models.get(modelName);
    }

    @Override
    public byte[] getModelFile(String modelName) {
        ModelPo modelPo = modelMapper.selectByModelName(modelName);
        if(modelPo==null) throw new ModelException("Model:"+ modelName+" not exist.");
        return modelPo.getModelBlob();
    }

    @Override
    public boolean checkModel(String modelName) {
        return modelName.contains(modelName);
    }
}
