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

package com.dubbo.postman.controller;

import com.dubbo.postman.dto.WebApiRspDto;
import com.dubbo.postman.service.load.LoadRuntimeInfo;
import com.dubbo.postman.repository.RedisRepository;
import com.dubbo.postman.service.appcreate.DubboAppCreator;
import com.dubbo.postman.service.appfind.zk.ZkServiceFactory;
import com.dubbo.postman.service.appfind.entity.InterfaceMetaInfo;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.dubbo.postman.service.dubboinvoke.TemplateBuilder;
import com.dubbo.postman.domain.DubboModel;
import com.dubbo.postman.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 提供服务的创建及刷新的功能
 * @author everythingbest
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class AppCreateController {

    private Logger logger = LoggerFactory.getLogger(AppCreateController.class);
    
    @Autowired
    private DubboAppCreator dubboAppCreator;
    
    @Autowired
    private RedisRepository redisRepository;

    @Resource
    TemplateBuilder templateBuilder;

    @Resource
    LoadRuntimeInfo loadJarClassService;

    @RequestMapping(value = "create",method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto createService(@RequestParam("zk") String zk,
                                      @RequestParam("zkServiceName") String serviceName,
                                      @RequestParam("dependency") String dependency){
    
        if(serviceName == null || serviceName.isEmpty()){
            return WebApiRspDto.error("必须选择一个服务名称",-1);
        }
        
        if(dependency == null || dependency.isEmpty()){
        
            return WebApiRspDto.error("dependency不能为空");
        }
    
        Map<String,String> dm = XmlUtil.parseDependencyXml(dependency);
        
        if(dm == null || dm.size() < 3){
    
            return WebApiRspDto.error("dependency格式不对,请指定正确的maven dependency,区分大小写");
        }
        
        String g = dm.get("groupId");
    
        String a = dm.get("artifactId");
    
        String v = dm.get("version");
        
        String versionDirName = v.replaceAll("\\.","_");

        String errorContent = "操作成功";

        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            LogResultPrintStream resultPrintStream = new LogResultPrintStream(stream);

            boolean prepare =  templateBuilder.mavenBuild(versionDirName+"_"+serviceName,g,a,v,resultPrintStream);

            byte[] bytes = resultPrintStream.getLogByteArray();

            errorContent = new String(bytes);

            if(!prepare){

                logger.warn(errorContent);

                return WebApiRspDto.error(errorContent);
            }

            Map<String, InterfaceMetaInfo> interfaceMetaInfoMap = ZkServiceFactory.get(zk).allProviders.get(serviceName);

            if(logger.isDebugEnabled()){

                logger.debug("应用名称:"+serviceName+"\n从ZK拉取的提供者:{} \n编译日志:\n {}", JSON.objectToString(interfaceMetaInfoMap),errorContent);
            }

            DubboModel dubboModel = dubboAppCreator.create(zk,serviceName,g,a,v,interfaceMetaInfoMap);

            buildRequestDubboTemplate(dubboModel);

        }catch (Exception exp){
            
            return WebApiRspDto.error(errorContent+"\n"+exp.getMessage());
        }
        
        return WebApiRspDto.success(errorContent);
    }

    @RequestMapping(value = "refresh",method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto refreshService(@RequestParam("zk") String zk,
                                        @RequestParam("zkServiceName") String serviceName){

        if(serviceName == null || serviceName.isEmpty()){

            return WebApiRspDto.error("必须选择一个服务名称",-1);
        }

        String modelKey = CommonUtil.getDubboModelKey(zk, serviceName);

        Object modelObj = redisRepository.mapGet(RedisKeys.DUBBO_MODEL_KEY, modelKey);

        DubboModel dubboModel = JSON.parseObject((String) modelObj,DubboModel.class);

        String g = dubboModel.getGroupId();

        String a = dubboModel.getArtifactId();

        String v = dubboModel.getVersion();

        String versionDirName = v.replaceAll("\\.","_");

        String errorContent = "操作成功";

        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            LogResultPrintStream resultPrintStream = new LogResultPrintStream(stream);

            boolean prepare =  templateBuilder.mavenBuild(versionDirName+"_"+serviceName,g,a,v,resultPrintStream);

            byte[] bytes = resultPrintStream.getLogByteArray();

            errorContent = new String(bytes);

            if(!prepare){

                logger.warn(errorContent);

                return WebApiRspDto.error(errorContent);
            }

            Map<String, InterfaceMetaInfo> interfaceMetaInfoMap = ZkServiceFactory.get(zk).allProviders.get(serviceName);

            if(logger.isDebugEnabled()){

                logger.debug("应用名称:"+serviceName+"\n从ZK拉取的提供者:{} \n编译日志:\n {}",JSON.objectToString(interfaceMetaInfoMap),errorContent);
            }

            dubboModel = dubboAppCreator.create(zk,serviceName,g,a,v,interfaceMetaInfoMap);

            buildRequestDubboTemplate(dubboModel);

        }catch (Exception exp){

            return WebApiRspDto.error(errorContent+"\n"+exp.getMessage());
        }

        return WebApiRspDto.success(errorContent);
    }

    private void buildRequestDubboTemplate(DubboModel model){

        loadJarClassService.load(model);

        persistent(model);

        templateBuilder.buildTemplateByDubboModel(model);

        templateBuilder.addTemplateRuntimeInfo(model);
    }

    private void persistent(DubboModel dubboModel){

        String modelString = JSON.objectToString(dubboModel);

        String modelKey = CommonUtil.getDubboModelKey(dubboModel.getZkAddress(), dubboModel.getServiceName());

        redisRepository.mapPut(RedisKeys.DUBBO_MODEL_KEY, modelKey, modelString);

        redisRepository.setAdd(dubboModel.getZkAddress(), dubboModel.getServiceName());
    }
}
