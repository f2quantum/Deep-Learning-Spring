package com.xrervip.super_ai_service.dao;
import ai.djl.repository.zoo.ZooModel;
import com.xrervip.super_ai_service.entity.ModelPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-28 19:59
 */
@Mapper
public interface ModelMapper {
    int insert(ModelPo record);

    ModelPo selectByModelName(@Param("modelName") String modelName);
}
