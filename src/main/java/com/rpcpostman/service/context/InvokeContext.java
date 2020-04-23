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

package com.rpcpostman.service.context;

import com.rpcpostman.service.creation.entity.RequestParam;
import com.rpcpostman.service.creation.entity.PostmanService;
import com.rpcpostman.service.invocation.Invocation;
import com.rpcpostman.service.invocation.entity.DubboInvocation;
import com.rpcpostman.service.invocation.entity.PostmanDubboRequest;
import com.rpcpostman.service.load.impl.JarLocalFileLoader;
import com.rpcpostman.service.Pair;
import com.rpcpostman.util.BuildUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author everythingbest
 */
public class InvokeContext {

    private static final Map<String, PostmanService> POSTMAN_SERVICE_MAP = new ConcurrentHashMap<>();

    private static final Map<String, List<RequestParam>> REQUESTPARAM_MAP = new ConcurrentHashMap<>();

    public static PostmanService getService(String serviceKey){
        PostmanService service = POSTMAN_SERVICE_MAP.getOrDefault(serviceKey,null);
        return service;
    }

    public static List<RequestParam> getRequestParam(String methodNameKey){
        List<RequestParam> requestParamList = REQUESTPARAM_MAP.getOrDefault(methodNameKey,null);
        return requestParamList;
    }

    public static void putService(String serviceKey, PostmanService service){
        POSTMAN_SERVICE_MAP.put(serviceKey,service);
    }

    public static void putMethod(String methodKey, List<RequestParam> requestParamList){
        REQUESTPARAM_MAP.put(methodKey,requestParamList);
    }

    public static void checkExistAndLoad(String cluster, String serviceName){
        String serviceKey = BuildUtil.buildServiceKey(cluster, serviceName);
        PostmanService postmanService = InvokeContext.getService(serviceKey);
        checkExistAndLoad(postmanService);
    }

    public static void checkExistAndLoad(PostmanService postmanService){
        if(postmanService == null){
            return;
        }
        //服务重启的时候需要重新构建运行时信息
        if(!postmanService.getLoadedToClassLoader()){
            JarLocalFileLoader.loadRuntimeInfo(postmanService);
        }
    }

    public static Pair<PostmanDubboRequest, Invocation> buildInvocation(String cluster,
                                                                        String serviceName,
                                                                        String interfaceKey,
                                                                        String methodName,
                                                                        String dubboParam,
                                                                        String dubboIp){


        checkExistAndLoad(cluster,serviceName);

        PostmanDubboRequest request = new PostmanDubboRequest();
        request.setCluster(cluster);
        request.setServiceName(serviceName);
        String group = BuildUtil.getGroupByInterfaceKey(interfaceKey);
        request.setGroup(group);
        String interfaceName = BuildUtil.getInterfaceNameByInterfaceKey(interfaceKey);
        request.setInterfaceName(interfaceName);
        String version = BuildUtil.getVersionByInterfaceKey(interfaceKey);
        request.setVersion(version);
        request.setMethodName(methodName);
        request.setDubboParam(dubboParam);
        request.setDubboIp(dubboIp);

        Invocation invocation = new DubboInvocation();
        String javaMethodName = BuildUtil.getJavaMethodName(methodName);
        invocation.setJavaMethodName(javaMethodName);
        String methodNameKey = BuildUtil.getMethodNameKey(cluster, serviceName, interfaceKey, methodName);
        List<RequestParam> requestParamList =  InvokeContext.getRequestParam(methodNameKey);
        invocation.setRequestParams(requestParamList);

        return new Pair<>(request,invocation);
    }
}
