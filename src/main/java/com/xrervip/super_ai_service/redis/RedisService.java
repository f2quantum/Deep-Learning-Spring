package com.xrervip.super_ai_service.redis;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    @Resource
    private RedisTemplate redisTemplate;


    public <T> void set(KeyPrefix prefix, String key, T value) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.opsForValue().set(realKey, value,prefix.expireSeconds(),TimeUnit.SECONDS);
    }

    public <T> T get(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return (T) redisTemplate.opsForValue().get(realKey);
    }

    public void delete(KeyPrefix prefix,String key) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.delete(realKey);
    }

    public void delete(KeyPrefix prefix,Collection<String> keys) {
        for (String key:keys){
            key = prefix.getPrefix() + key;
        }
        redisTemplate.delete(keys);
    }

    public boolean expire(KeyPrefix prefix,String key, long time) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.expire(realKey, time, TimeUnit.SECONDS);
    }

    public Long getExpire(KeyPrefix prefix,String key) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.getExpire(realKey, TimeUnit.SECONDS);
    }

    public boolean hasKey(KeyPrefix prefix,String key) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.hasKey(realKey);
    }

    public Long increment(KeyPrefix prefix,String key, long delta) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realKey, delta);
    }

    public Long decrement(KeyPrefix prefix,String key, long delta) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realKey, -delta);
    }

    public <T> void addSet(KeyPrefix prefix,String key, T value) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.opsForSet().add(realKey, value);
        redisTemplate.expire(realKey, prefix.expireSeconds(), TimeUnit.SECONDS);
    }

    public <T> Set<T> getSet(KeyPrefix prefix,String key) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.opsForSet().members(realKey);
    }

    public <T> void deleteSet(KeyPrefix prefix,String key, T value) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.opsForSet().remove(realKey, value);
    }


}
