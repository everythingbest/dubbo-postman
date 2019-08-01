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

import com.dubbo.postman.domain.*;
import com.dubbo.postman.repository.LocalStore;
import com.dubbo.postman.util.LogResultPrintStream;
import com.dubbo.postman.util.CommonUtil;
import com.dubbo.postman.service.maven.MavenProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 根据dubbo model构建template
 * @author everythingbest
 */
@Service
public class TemplateBuilder {

    private Logger logger = LoggerFactory.getLogger(TemplateBuilder.class);

    @Resource
    MavenProcessor mavenProcess;

    public void buildTemplateByDubboModel(DubboModel model){

        for(DubboInterfaceModel interfaceModel : model.getServiceModelList()){

            for(DubboMethodModel methodModel : interfaceModel.getMethods()){

                String methodName = methodModel.getName();

                //一个template对应一个唯一的方法访问
                RequestTemplate template = new RequestTemplate();

                template.setInterfaceName(interfaceModel.getInterfaceName());

                template.setDubboUrl("dubbo" + "://" + "ip");

                template.setRetries(interfaceModel.getRetries());

                String zkRegistry = CommonUtil.buildZkUrl(model.getZkAddress());

                template.setRegistry(zkRegistry);

                template.setServiceName(model.getServiceName());

                template.setVersion(interfaceModel.getVersion());

                template.setGroup(interfaceModel.getGroup());

                String requestPath = model.getServiceName() + "/" + interfaceModel.getGroup() + "/"
                        + interfaceModel.getInterfaceName().replace(".", "/");

                if (!interfaceModel.getVersion().isEmpty()) {

                    requestPath += "/" + interfaceModel.getVersion().replace(".", "/");
                }

                requestPath = "/" + requestPath + "/" + methodName;

                LocalStore.put(requestPath, template, model);
            }
        }
    }

    public void addTemplateRuntimeInfo(DubboModel dubboModel){

        //加载这个服务的所有方法
        for(DubboInterfaceModel interfaceModel : dubboModel.getServiceModelList()){

            for(DubboMethodModel methodModel : interfaceModel.getMethods()){

                String path = requestPath(dubboModel,interfaceModel,methodModel.getName());

                RequestTemplate template = LocalStore.get(path);

                if(template != null){

                    addMethodWithParameters(methodModel,template);

                    template.setHasRunTimeInfo(true);
                }
            }
        }
    }

    String requestPath(DubboModel dubboModel, DubboInterfaceModel interfaceModel, String methodName){

        String realPath =
                dubboModel.getServiceName() + "/" + interfaceModel.getGroup() + "/"
                        + interfaceModel.getInterfaceName().replace(".", "/");

        if (!interfaceModel.getVersion().isEmpty()) {
            realPath += "/" + interfaceModel.getVersion().replace(".", "/");
        }

        realPath = "/" + realPath + "/" + methodName;

        return realPath;
    }

    void addMethodWithParameters(DubboMethodModel methodModel, RequestTemplate template){

        template.setMethodName(methodModel.getMethod().getName());

        //设置参数类型及名称
        if (methodModel.getMethod().getParameterTypes().length >= 1) {

            int paramIndex = 0;

            for (Class<?> type : methodModel.getMethod().getParameterTypes()) {

                RequestParam requestParam = new RequestParam();

                requestParam.setParaName(methodModel.getParams().get(paramIndex).getName());

                paramIndex++;

                requestParam.setTargetParaType(type);

                template.getMatcherParams().add(requestParam);
            }
        }
    }

    public boolean mavenBuild(
            String serviceDirName,
            String groupId,
            String artifactId,
            String version,
            LogResultPrintStream resultPrintStream) {

        logger.info("开始创建服务...");

        logger.info("如果系统是第一次构建服务则需要下载各种maven plugin,耗时比较长");

        boolean prepare = mavenProcess.process(serviceDirName,groupId,artifactId,version,resultPrintStream);

        return prepare;
    }
}
