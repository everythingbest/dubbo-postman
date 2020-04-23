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

package com.rpcpostman.service.load.impl;

import com.rpcpostman.service.creation.entity.InterfaceEntity;
import com.rpcpostman.service.creation.entity.MethodEntity;
import com.rpcpostman.service.creation.entity.ParamEntity;
import com.rpcpostman.service.creation.entity.RequestParam;
import com.rpcpostman.service.creation.entity.PostmanService;
import com.rpcpostman.service.context.InvokeContext;
import com.rpcpostman.service.load.classloader.ApiJarClassLoader;
import com.rpcpostman.service.load.Loader;
import com.rpcpostman.util.BuildUtil;
import com.rpcpostman.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author everythingbest
 *
 * 通过ApiJarClassLoader来实现api.jar的加载
 */
public class JarLocalFileLoader implements Loader {
    
    private static final Logger logger = LoggerFactory.getLogger(JarLocalFileLoader.class);
    private static final Map<String, ApiJarClassLoader> loaderMap = new ConcurrentHashMap<>();

    public static void loadRuntimeInfo(PostmanService service) {

        String apiJarPath = System.getProperty(Constant.USER_HOME);
        String serviceName = service.getServiceName();
        String v = service.getGav().getVersion();
        String versionDirName = v.replaceAll("\\.","_");

        File dir = new File(apiJarPath + File.separator +versionDirName+"_"+ serviceName);
        List<URL> urlList = getUrls(dir.getAbsolutePath()+File.separator+"lib");

        doLoad(urlList,service);
    }

    public static Map<String, ApiJarClassLoader> getAllClassLoader() {
        return loaderMap;
    }

    private static List<URL> getUrls(String jarPath){
        
        File baseFile = new File(jarPath);
        List<URL> urlList = new ArrayList<>();
    
        for(File file : baseFile.listFiles()){
            URL url =  getFileUrls(file);
            urlList.add(url);
        }
        return urlList;
    }

    private static URL getFileUrls(File jarFile){
        URL urls = null;
        try {
            urls = jarFile.toURI().toURL();
        } catch (MalformedURLException e) {
            logger.warn(jarFile.getAbsolutePath()+"转换为url失败",e);
        }
        return urls;
    }

    /**
     * 通过ApiJarClassLoader加载所有的接口,同时解析接口里面的所有方法构建运行时类信息,添加到invokeContext里面
     * @param urlList
     * @param service
     */
    private static void doLoad(List<URL> urlList,PostmanService service){

        ApiJarClassLoader jarFileClassLoader = new ApiJarClassLoader(urlList.toArray(new URL[]{}));

        String serviceKey = BuildUtil.buildServiceKey(service.getCluster(), service.getServiceName());
        loaderMap.put(serviceKey,jarFileClassLoader);

        for(InterfaceEntity interfaceModel : service.getInterfaceModelList()){

            Set<String> methodNames = interfaceModel.getMethodNames();
            try {
                Class<?> clazz = jarFileClassLoader.loadClassWithResolve(interfaceModel.getInterfaceName());
                Method[] methods = clazz.getDeclaredMethods();
            
                //清空之前的内容，应用在重启的时候会重新load class,所以需要把原来的clear,再加进去一次，这个以后可以优化
                interfaceModel.getMethods().clear();

                for(Method method : methods){
                    //只加载应用注册到zk里面的方法
                    if(!methodNames.contains(method.getName()) || !Modifier.isPublic(method.getModifiers())){
                        continue;
                    }

                    MethodEntity methodModel = new MethodEntity();
                    //设置运行时信息
                    methodModel.setMethod(method);

                    StringBuilder paramStr = new StringBuilder("(");
                    List<RequestParam> requestParamList = new ArrayList<>();
                    //如果没有参数，表示空参数，在调用的时候是空数组，没有问题
                    for(Parameter parameter : method.getParameters()){

                        ParamEntity paramModel = new ParamEntity();
                        paramModel.setName(parameter.getName());
                        paramModel.setType(parameter.getParameterizedType().getTypeName());

                        String wholeName = parameter.getParameterizedType().getTypeName();
                        String simpleName = wholeName.substring(wholeName.lastIndexOf(".")+1);
                        paramStr.append(simpleName+",");

                        methodModel.getParams().add(paramModel);

                        RequestParam requestParam = new RequestParam();
                        requestParam.setParaName(parameter.getName());
                        requestParam.setTargetParaType(parameter.getType());
                        requestParamList.add(requestParam);
                    }

                    String allParamsName;
                    if(paramStr.length() == 1){
                        allParamsName = paramStr+")";
                    }else{
                        allParamsName = paramStr.substring(0,paramStr.length()-1)+")";
                    }

                    interfaceModel.getMethods().add(methodModel);
                    String extendMethodName = method.getName() + allParamsName;
                    methodModel.setName(extendMethodName);

                    String methodKey = BuildUtil.buildMethodNameKey(service.getCluster(),
                            service.getServiceName(),
                            interfaceModel.getGroup(),
                            interfaceModel.getInterfaceName(),
                            interfaceModel.getVersion(),
                            methodModel.getName());

                    InvokeContext.putMethod(methodKey, requestParamList);
                }

                //设置运行时信息,主要用于在页面请求的时候可以通过json序列化为json string的格式
                interfaceModel.setInterfaceClass(clazz);
            } catch (Throwable e) {
                logger.warn(interfaceModel.getInterfaceName()+"加载到内存失败:"+e);
            }
        }

        service.setLoadedToClassLoader(true);
    }
}
