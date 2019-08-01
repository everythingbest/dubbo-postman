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

package com.dubbo.postman.service.appcreate;

import com.dubbo.postman.service.appfind.entity.InterfaceMetaInfo;
import com.dubbo.postman.domain.DubboInterfaceModel;
import com.dubbo.postman.domain.DubboModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author everythingbest
 * 用于根据zk提供的基础数据和下载的api.jar生成运行时的dubboModel
 */
@Service
public class DubboAppCreator {
    
    public DubboModel create(String zk, String serviceName, String g, String a, String v, Map<String, InterfaceMetaInfo> providers){
    
        DubboModel dubboModel = metaItemToDubboModel(serviceName,g,a,v,providers);

        dubboModel.setZkAddress(zk);

        dubboModel.setZk(true);

        return dubboModel;
    }
    
    /**
     * 从zk拉取提供者相关的信息,然后构建成一个dubboModel
     * @param serviceName
     * @param g
     * @param a
     * @param v
     * @param providers
     * @return
     */
    private DubboModel metaItemToDubboModel(String serviceName,
                                            String g,
                                            String a,
                                            String v,
                                            Map<String, InterfaceMetaInfo> providers){
        

        DubboModel dubboModel = new DubboModel();

        dubboModel.setServiceName(serviceName);

        for(Map.Entry<String, InterfaceMetaInfo> entry : providers.entrySet()){

            DubboInterfaceModel dubboInterfaceModel = new DubboInterfaceModel();

            String providerName = entry.getValue().getInterfaceName();

            String version = entry.getValue().getVersion();

            Set<String> serverIps = entry.getValue().getServerIps();

            Set methodNames = entry.getValue().getMethodNames();

            dubboInterfaceModel.setKey( entry.getKey());

            dubboInterfaceModel.setInterfaceName(providerName);

            dubboInterfaceModel.setMethodNames(methodNames);

            dubboInterfaceModel.setServerIps(serverIps);

            dubboInterfaceModel.setVersion(version);

            dubboInterfaceModel.setGroup(entry.getValue().getGroup());

            dubboModel.getServiceModelList().add(dubboInterfaceModel);
        }

        dubboModel.setGroupId(g);

        dubboModel.setArtifactId(a);

        dubboModel.setVersion(v);

        dubboModel.setGenerateTime(System.currentTimeMillis());

        return dubboModel;
    }
}
