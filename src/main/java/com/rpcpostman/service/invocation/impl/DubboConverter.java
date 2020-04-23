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

package com.rpcpostman.service.invocation.impl;

import com.rpcpostman.service.creation.entity.RequestParam;
import com.rpcpostman.service.invocation.entity.DubboParamValue;
import com.rpcpostman.service.invocation.entity.PostmanDubboRequest;
import com.rpcpostman.service.invocation.exception.ParamException;
import com.rpcpostman.service.invocation.Converter;
import com.rpcpostman.service.invocation.Invocation;
import com.rpcpostman.util.BuildUtil;
import com.rpcpostman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author everythingbest
 */
@Component
class DubboConverter implements Converter<PostmanDubboRequest, DubboParamValue> {

    private static final Logger logger = LoggerFactory.getLogger(DubboConverter.class);

    @Override
    public DubboParamValue convert(PostmanDubboRequest request, Invocation invocation) throws ParamException{

        Map<String,Object> bodyMap = JSON.parseObject(request.getDubboParam(), Map.class);

        if(bodyMap == null){
            throw new ParamException("请求参数不能为空");
        }

        DubboParamValue  rpcParamValue = new DubboParamValue();
        //遍历模板的参数名称
        for(RequestParam param : invocation.getParams()){

            String paramName = param.getParaName();
            Class<?> targetType = param.getTargetParaType();

            boolean nullParam = false;
            Object paramValue = null;

            Object value = bodyMap.get(paramName);

            if(value == null){
                //传入null的参数
                nullParam = true;
            }else{

                ClassLoader old = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(targetType.getClassLoader());

                try {
                    paramValue = JSON.mapper.convertValue(value, targetType);
                }catch (Exception exp){
                    logger.error("参数反序列化失败:"+exp.getMessage());
                }finally {
                    Thread.currentThread().setContextClassLoader(old);
                }
            }

            if(!nullParam && paramValue == null){
                throw new ParamException("参数匹配错误,参数名称:"+paramName+",请检查类型,参数类型:"+targetType.getName());
            }

            rpcParamValue.addParamTypeName(targetType.getName());
            rpcParamValue.addParamValue(paramValue);
        }

        parseExternalParams(request,rpcParamValue);

        return rpcParamValue;
    }

    private void parseExternalParams(PostmanDubboRequest request, DubboParamValue  rpcParamValue) {

        if(request.getDubboIp() != null && !request.getDubboIp().isEmpty()){
            String dubboIp = request.getDubboIp();
            rpcParamValue.setDubboUrl(rpcParamValue.getDubboUrl().replace("ip",dubboIp));
            rpcParamValue.setUseDubbo(true);
        }

        if(request.getCluster() != null){
            String zk = request.getCluster();
            String accessZk = BuildUtil.buildZkUrl(zk);
            rpcParamValue.setRegistry(accessZk);
        }
    }
}
