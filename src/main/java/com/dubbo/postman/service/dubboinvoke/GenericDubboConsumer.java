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

package com.dubbo.postman.service.dubboinvoke;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.dubbo.postman.dto.WebApiRspDto;
import com.dubbo.postman.domain.RequestTemplate;
import com.dubbo.postman.util.Constant;
import com.dubbo.postman.util.ExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 统一的消费dubbo的服务
 * @author everythingbest
 */
@Service
public class GenericDubboConsumer {

    private static Logger logger = LoggerFactory.getLogger(GenericDubboConsumer.class);

    private ApplicationConfig application = new ApplicationConfig(Constant.APP_NAME);

    private Map<String,ReferenceConfig<GenericService>> cachedReference = new WeakHashMap<>();

    @Resource
    TemplateFetcher paramParser;

    /**
     * 处理所有请求
     * @param request
     */
    public WebApiRspDto send(Request request) {

        if(request.getPath() == null){

            return WebApiRspDto.error("必须选择一个接口和方法进行访问",ResponseCode.SYSTEM_ERROR.code);
        }

        logger.info("请求路径:"+request.getPath());

        RequestTemplate template;

        try {

            template = paramParser.getTemplate(request);

        }catch (Exception exp){

            String expStr = ExceptionHelper.getExceptionStackString(exp);

            return WebApiRspDto.error("解析参数错误:"+expStr,ResponseCode.SYSTEM_ERROR.code);
        }

        try {

            GenericService service = this.getOrCreateService(template);

            String methodName = template.getMethodName();

            String[] parameterTypes = template.getParamTypes().toArray(new String[]{});

            Object[] parameterValues = template.getParamValues().toArray(new Object[]{});

            long start = System.currentTimeMillis();

            Object obj = service.$invoke(methodName, parameterTypes, parameterValues);

            long end = System.currentTimeMillis();

            long elapse = end - start;

            logger.info("请求dubbo耗时:"+elapse);

            return WebApiRspDto.success(obj,elapse);

        }catch (Exception exp){

            logger.warn("请求dubbo服务失败",exp);

            String exceptionStr = ExceptionHelper.getExceptionStackString(exp);

            return WebApiRspDto.error(exceptionStr,ResponseCode.APP_ERROR.code);
        }
    }

    private synchronized GenericService getOrCreateService(RequestTemplate template) throws Exception {

        String serviceName = template.getServiceName();

        String group = template.getGroup();

        String interfaceName = template.getInterfaceName();

        String referenceKey = serviceName + "-" + group +"-"+ interfaceName;

        String cacheKey;

        if(template.isUseDubbo()){

            cacheKey = template.getDubboUrl()+"-"+referenceKey;

        }else{

            cacheKey = template.getRegistry()+"-"+referenceKey;
        }

        ReferenceConfig<GenericService> reference = cachedReference.get(cacheKey);

        if(reference != null ){

            GenericService service = reference.get();

            //如果创建失败了,比如provider重启了,需要重新创建
            if(service == null){

                cachedReference.remove(cacheKey);

                throw new Exception("ReferenceConfig创建GenericService失败,检查provider是否启动");
            }

            return service;

        }else{

            ReferenceConfig<GenericService> newReference = createReference(template);

            GenericService service = newReference.get();

            //如果创建成功了就添加，否则不添加
            if(service != null){

                cachedReference.put(cacheKey,newReference);
            }

            return service;
        }
    }

    ReferenceConfig<GenericService> createReference(RequestTemplate template){

        ReferenceConfig<GenericService> newReference = new ReferenceConfig<GenericService>();

        //设置默认超时无限制,用于在本地调试的时候用
        newReference.setTimeout(Integer.MAX_VALUE);

        newReference.setApplication(application);

        newReference.setInterface(template.getInterfaceName());

        String group = template.getGroup();

        //default是我加的,dubbo默认是没有的
        if(group.isEmpty() || group.equals("default")){

        }else{

            newReference.setGroup(group);
        }

        if(template.isUseDubbo()){

            //直连
            newReference.setUrl(template.getDubboUrl());

            logger.info("直连dubbo地址:"+template.getDubboUrl());

        }else{

            //通过zk访问
            newReference.setRegistry(new RegistryConfig(template.getRegistry()));
        }

        newReference.setVersion(template.getVersion());

        newReference.setGeneric(true);

        newReference.setRetries(template.getRetries());

        return newReference;
    }
}
