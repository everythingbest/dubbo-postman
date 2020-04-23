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

package com.rpcpostman.controller;

import com.rpcpostman.service.creation.entity.DubboPostmanService;
import com.rpcpostman.service.repository.redis.RedisRepository;
import com.rpcpostman.service.creation.entity.PostmanService;
import com.rpcpostman.service.creation.entity.InterfaceEntity;
import com.rpcpostman.service.repository.redis.RedisKeys;
import com.rpcpostman.util.JSON;
import com.rpcpostman.util.BuildUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author everythingbest
 * 提供一些公共的模板方法
 */
@Service
public abstract class AbstractController {

    @Autowired
    private RedisRepository cacheService;

    Map<String,String> getAllSimpleClassName(String zk, String serviceName){

        String modelKey = BuildUtil.buildServiceKey(zk,serviceName);
        Object object = cacheService.mapGet(RedisKeys.RPC_MODEL_KEY,modelKey);
        Map<String,String> interfaceMap = new HashMap<>(10);
        PostmanService dubboModel = JSON.parseObject((String)object, DubboPostmanService.class);

        for(InterfaceEntity interfaceModel : dubboModel.getInterfaceModelList()){

            String className = interfaceModel.getInterfaceName();
            String simpleClassName = className.substring(className.lastIndexOf(".")+1);

            interfaceMap.put(simpleClassName,interfaceModel.getKey());
        }

        return interfaceMap;
    }
}
