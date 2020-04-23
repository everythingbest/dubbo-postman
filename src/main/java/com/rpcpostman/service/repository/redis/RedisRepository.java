/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.rpcpostman.service.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author everythingbest
 * 封装redis相关的操作
 */
@Repository
public class RedisRepository implements com.rpcpostman.service.repository.Repository {

    @Autowired
    private RedisTemplate redisTemplate;

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void setAdd(String key,Object value){
        redisTemplate.opsForSet().add(key,value);
    }

    public Set<Object> members(String key){
        Set<Object> sets = redisTemplate.opsForSet().members(key);
        return sets;
    }

    public long setRemove(String key,Object value){
        long count = redisTemplate.opsForSet().remove(key,value);
        return count;
    }


    public void mapPut(String key,Object hashKey,Object value){
        redisTemplate.opsForHash().put(key,hashKey,value);
        redisTemplate.persist(key);
    }

    public Object mapGet(String key,Object hashKey){
        return redisTemplate.opsForHash().get(key,hashKey);
    }

    public Set<Object> mapGetKeys(String key){
        return redisTemplate.opsForHash().keys(key);
    }

    public List<Object> mapGetValues(String key){
        List<Object> lists = redisTemplate.opsForHash().values(key);
        return lists;
    }

    public void removeMap(String key,String hashKey){
        redisTemplate.opsForHash().delete(key,hashKey);
    }
}
