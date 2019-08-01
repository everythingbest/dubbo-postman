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
import com.dubbo.postman.repository.LocalStore;
import com.dubbo.postman.service.load.ApiJarClassLoader;
import com.dubbo.postman.service.load.LoadRuntimeInfo;
import com.dubbo.postman.repository.RedisRepository;
import com.dubbo.postman.service.appfind.zk.ZkServiceFactory;
import com.dubbo.postman.service.appfind.entity.InterfaceMetaInfo;
import com.dubbo.postman.domain.DubboInterfaceModel;
import com.dubbo.postman.domain.DubboMethodModel;
import com.dubbo.postman.domain.DubboModel;
import com.dubbo.postman.domain.DubboParamModel;
import com.dubbo.postman.util.CommonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author everythingbest
 * 应用详细信息查询相关
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class AppDetailController extends AbstractController{
    
    @Autowired
    private RedisRepository cacheService;

    @Autowired
    LoadRuntimeInfo loadJarClassService;

    @RequestMapping(value = "all-zk", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto allZk() {
        
        Set<String> zkAddrs = ZkServiceFactory.ZK_SET;
        
        return WebApiRspDto.success(zkAddrs);
    }
    
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

        //应用名称,provider名称,provider的其他属性信息
        Map<String,Map<String, InterfaceMetaInfo>> services = ZkServiceFactory.get(zk).allProviders;

        return WebApiRspDto.success(services.keySet());
    }
    
    /**
     * 返回已经注册的服务名称
     * @param zk 指定的zk地址
     * @return 返回指定zk下面的所有已经注册的服务名称
     */
    @RequestMapping(value = "result/serviceNames",method = {RequestMethod.GET})
    @ResponseBody
    public WebApiRspDto getServiceName(@RequestParam(value = "zk") String zk){
        
        Set<Object> serviceNameSet = cacheService.members(zk);
        
        return WebApiRspDto.success(serviceNameSet);
    }

    /**
     * 返回所有的接口
     * @param serviceName
     * @param interfaceKey  接口key group/interfaceName:version
     * @return
     */
    @RequestMapping(value = "result/interface",method = {RequestMethod.GET})
    @ResponseBody
    public WebApiRspDto getInterfaces(@RequestParam("zk") String zk,
                                      @RequestParam("serviceName") String serviceName,
                                      @RequestParam("interfaceKey") String interfaceKey){


        String modelKey = CommonUtil.getDubboModelKey(zk, serviceName);

        DubboModel dubboModel = LocalStore.DUBBO_MODEL_MAP.get(modelKey);

        if(dubboModel == null){

            return WebApiRspDto.error("服务不存在,请先创建或刷新服务!");
        }

        dubboModel.setServiceName(serviceName);

        for(DubboInterfaceModel interfaceModel : dubboModel.getServiceModelList()){

            InterfaceMetaInfo metaItem = ZkServiceFactory.get(zk).allProviders.get(serviceName).get(interfaceKey);

            //根据接口名称匹配接口对应的服务
            if(interfaceModel.getKey().equals(interfaceKey)){

                //用于实时同步应用的所有ip
                interfaceModel.setServerIps(metaItem.getServerIps());

                return WebApiRspDto.success(interfaceModel);
            }
        }

        return WebApiRspDto.error("查找接口对应的服务异常");
    }
    
    /**
     * 根据服务名称,接口key,方法路径,获取参数模板
     * @param serviceName
     * @param interfaceKey
     * @param methodPath
     * @return
     */
    @RequestMapping(value = "result/interface/method/param",method = {RequestMethod.GET})
    @ResponseBody
    public WebApiRspDto getResultServiceMethod(@RequestParam("zk") String zk,
                                                @RequestParam("serviceName") String serviceName,
                                                 @RequestParam("interfaceKey") String interfaceKey,
                                                 @RequestParam("methodPath") String methodPath){

        String modelKey = CommonUtil.getDubboModelKey(zk, serviceName);

        DubboModel dubboModel = LocalStore.DUBBO_MODEL_MAP.get(modelKey);

        if(dubboModel == null){

            return WebApiRspDto.error("服务不存在,请先创建或刷新服务!");
        }

        Map<String,Object> param = Maps.newLinkedHashMap();

        if(!dubboModel.getLoadedToClassLoader()){
            try {
                //当重新发布的情况会存在未空的情况
                loadJarClassService.load(dubboModel);

            } catch (Exception e) {

                return WebApiRspDto.error(e.getMessage());
            }
        }
        //重启之后在访问的时候重新load
        ApiJarClassLoader loader = loadJarClassService.getClassLoader(dubboModel);

        if(loader == null){

            return WebApiRspDto.error("加载类异常,classLoader为null");
        }

        for(DubboInterfaceModel serviceModel : dubboModel.getServiceModelList()){

            if(!serviceModel.getKey().equals(interfaceKey)){

                continue;
            }

            for(DubboMethodModel methodModel : serviceModel.getMethods()){

                if(!methodModel.getName().equals(methodPath)){

                    continue;
                }

                for(DubboParamModel paramModel : methodModel.getParams()){

                    boolean primitiveValue = isPrimitive(paramModel.getType());

                    if(primitiveValue){

                        param.put(paramModel.getName(),null);

                    }else{

                        setParams(paramModel,loader,param);
                    }
                }
            }
        }

        return WebApiRspDto.success(param);
    }

    void setParams(DubboParamModel paramModel, ApiJarClassLoader loader, Map<String,Object> param){

        //集合类型,寻求更好的处理方式,泛型集合
        if(paramModel.getType().contains("<")){

            String collectionName = paramModel.getType();

            String genericObjectName = collectionName.substring(collectionName.indexOf("<")+1,collectionName.indexOf(">"));

            try {

                Class clazz = Class.forName(genericObjectName,true,loader);

                Object clazzObject = clazz.newInstance();

                List list = Lists.newArrayList();

                if(clazzObject != null){

                    list.add(clazzObject);
                }

                param.put(paramModel.getName(),list);

            } catch (Exception e) {

                param.put(paramModel.getName(),null);
            }

        }else{

            try {

                Class clazz = loader.loadClassWithResolve(paramModel.getType());

                Object clazzObject = clazz.newInstance();

                param.put(paramModel.getName(),clazzObject);

            } catch (Exception e) {

                param.put(paramModel.getName(),null);

            }
        }
    }

    /**
     * 返回所有的接口
     * @param serviceName 服务名称
     * @return
     */
    @RequestMapping(value = "result/interfaceNames",method = {RequestMethod.GET})
    @ResponseBody
    public WebApiRspDto getInterfaces(@RequestParam("zk") String zk,@RequestParam("serviceName") String serviceName){

        try {

            Map<String, String> interfaceMap = getAllClassName(zk, serviceName);

            return WebApiRspDto.success(interfaceMap);

        }catch (Exception exp){

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    private boolean isPrimitive(String typeName){
        switch (typeName) {
            case "int" :
                return true;
            case "char":
                return true;
            case "long":
                return true;
            case "boolean":
                return true;
            case "float":
                return true;
            case "double":
                return true;
            case "[B":
            case "byte[]":
                return true;
            case "java.lang.String":
                return true;
            case "java.lang.Integer":
                return true;
            case "java.lang.Long":
                return true;
            case "java.lang.Double":
                return true;
            case "java.lang.Float":
                return true;
            default:
                return false;
        }
    }
}
