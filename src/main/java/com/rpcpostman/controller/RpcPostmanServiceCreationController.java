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

import com.rpcpostman.dto.WebApiRspDto;
import com.rpcpostman.service.GAV;
import com.rpcpostman.service.Pair;
import com.rpcpostman.service.creation.Creator;
import com.rpcpostman.service.registry.entity.InterfaceMetaInfo;

import java.util.Map;

import com.rpcpostman.service.registry.impl.DubboRegisterFactory;
import com.rpcpostman.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 提供服务的创建及刷新的功能
 * @author everythingbest
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class RpcPostmanServiceCreationController {

    private final Logger logger = LoggerFactory.getLogger(RpcPostmanServiceCreationController.class);

    @Autowired
    private Creator creator;

    /**
     * 根据zk,返回所有的应用名称,这个是zk里面的,还未创建
     * @param zk
     * @return
     */
    @RequestMapping(value = "result/appNames",method = {RequestMethod.GET})
    @ResponseBody
    public WebApiRspDto getAppNames(@RequestParam("zk") String zk){

        if(zk.isEmpty()){
            return WebApiRspDto.error("zk地址必须指定");
        }
        Map<String,Map<String, InterfaceMetaInfo>> services = DubboRegisterFactory.getInstance().get(zk).getAllService();
        return WebApiRspDto.success(services.keySet());
    }

    @RequestMapping(value = "create",method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<String> createService(@RequestParam("zk") String zk,
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

        GAV gav = new GAV();
        gav.setGroupID(g);
        gav.setArtifactID(a);
        gav.setVersion(v);

        Pair<Boolean, String> pair = creator.create(zk, gav, serviceName);
        if(!pair.getLeft()){
            return WebApiRspDto.error(pair.getRight());
        }
        return WebApiRspDto.success(pair.getRight());
    }

    @RequestMapping(value = "refresh",method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto refreshService(@RequestParam("zk") String zk,
                                        @RequestParam("zkServiceName") String serviceName){

        if(serviceName == null || serviceName.isEmpty()){

            return WebApiRspDto.error("必须选择一个服务名称",-1);
        }

        Pair<Boolean, String> pair = creator.refresh(zk, serviceName);
        if(!pair.getLeft()){
            return WebApiRspDto.error(pair.getRight());
        }
        return WebApiRspDto.success(pair.getRight());
    }
}
