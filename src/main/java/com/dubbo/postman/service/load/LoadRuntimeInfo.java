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

package com.dubbo.postman.service.load;

import com.dubbo.postman.domain.DubboInterfaceModel;
import com.dubbo.postman.domain.DubboMethodModel;
import com.dubbo.postman.domain.DubboModel;
import com.dubbo.postman.domain.DubboParamModel;
import com.dubbo.postman.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
@Service
public class LoadRuntimeInfo {
    
    private static Logger logger = LoggerFactory.getLogger(LoadRuntimeInfo.class);

    private static final Map<String, ApiJarClassLoader> loaderMap = new ConcurrentHashMap<>();

    public void load(DubboModel dubboModel){

        String apiJarPath = System.getProperty(Constant.USER_HOME);

        String serviceName = dubboModel.getServiceName();

        String v = dubboModel.getVersion();

        String versionDirName = v.replaceAll("\\.","_");

        File dir = new File(apiJarPath + File.separator +versionDirName+"_"+ serviceName);

        load(dir.getAbsolutePath()+File.separator+"lib",dubboModel);

        dubboModel.setLoadedToClassLoader(true);
    }

    void load(String jarPath, DubboModel dubboModel){
        
        File baseFile = new File(jarPath);

        List<URL> urlList = new ArrayList<>();
    
        for(File file : baseFile.listFiles()){
            
            URL url =  getFileUrls(file);
    
            urlList.add(url);
        }
        
        doLoad(urlList,dubboModel);
    }

    public ApiJarClassLoader getClassLoader(DubboModel dubboModel){

        String key = classLoaderKey(dubboModel);

        return loaderMap.get(key);
    }

    String classLoaderKey(DubboModel dubboModel){

        return dubboModel.getVersion()+"_"+dubboModel.getServiceName();
    }

    /**
     * 通过ApiJarClassLoader加载所有的接口,同时解析接口里面的所有方法构建DubboMethodModel,添加到DubboModel里面
     * @param urlList
     * @param dubboModel
     */
    void doLoad(List<URL> urlList,DubboModel dubboModel){

        ApiJarClassLoader jarFileClassLoader = new ApiJarClassLoader(urlList.toArray(new URL[]{}));

        String key = classLoaderKey(dubboModel);

        loaderMap.put(key,jarFileClassLoader);

        for(DubboInterfaceModel serviceModel : dubboModel.getServiceModelList()){
            
            Set<String> methodNames = serviceModel.getMethodNames();
            
            try {
            
                Class<?> clazz = jarFileClassLoader.loadClassWithResolve(serviceModel.getInterfaceName());

                Method[] methods = clazz.getDeclaredMethods();
            
                //清空之前的内容，应用在重启的时候会重新load class,所以需要把原来的clear,再加进去一次，这个以后可以优化
                serviceModel.getMethods().clear();

                for(Method method : methods){
                    
                    //只加载应用注册到zk里面的方法
                    if(!methodNames.contains(method.getName()) || !Modifier.isPublic(method.getModifiers())){
                        
                        continue;
                    }

                    DubboMethodModel methodModel = new DubboMethodModel();

                    //设置运行时信息
                    methodModel.setMethod(method);

                    StringBuilder paramStr = new StringBuilder("(");

                    for(Parameter parameter : method.getParameters()){

                        DubboParamModel paramModel = new DubboParamModel();

                        paramModel.setName(parameter.getName());

                        paramModel.setType(parameter.getParameterizedType().getTypeName());

                        String wholeName = parameter.getParameterizedType().getTypeName();

                        String simpleName = wholeName.substring(wholeName.lastIndexOf(".")+1);

                        paramStr.append(simpleName+",");

                        methodModel.getParams().add(paramModel);
                    }

                    String allParamsName;

                    if(paramStr.length() == 1){

                        allParamsName = paramStr+")";

                    }else{

                        allParamsName = paramStr.substring(0,paramStr.length()-1)+")";
                    }

                    serviceModel.getMethods().add(methodModel);

                    String extendMethodName = method.getName() + allParamsName;

                    methodModel.setName(extendMethodName);
                }

                //设置运行时信息
                serviceModel.setInterfaceClass(clazz);
                
            } catch (Throwable e) {
                
                logger.warn(serviceModel.getInterfaceName()+"加载到内存失败:"+e);
            }
        }
    }

    private URL getFileUrls(File jarFile){

        URL urls = null;

        try {

            urls = jarFile.toURI().toURL();

        } catch (MalformedURLException e) {

            logger.warn(jarFile.getAbsolutePath()+"转换为url失败",e);
        }

        return urls;
    }
}
