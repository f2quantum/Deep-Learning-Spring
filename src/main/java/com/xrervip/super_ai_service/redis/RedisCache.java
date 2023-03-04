package com.xrervip.super_ai_service.redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: frzquantum@gmail.com
 * DateTime: 2023-02-27 21:16
 */
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

}
