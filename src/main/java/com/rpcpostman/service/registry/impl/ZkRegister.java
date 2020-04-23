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

package com.rpcpostman.service.registry.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.rpcpostman.service.registry.entity.InterfaceMetaInfo;
import com.rpcpostman.service.registry.Register;
import com.rpcpostman.util.BuildUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author everythingbest
 */
public class ZkRegister implements Register {

    private Logger logger = LoggerFactory.getLogger(ZkRegister.class);

    private final Map<String,Map<String, InterfaceMetaInfo>> allProviders = new ConcurrentHashMap();

    private final ZkClient client;

    final static String dubboRoot = "/dubbo";

    private final Map<String,IZkChildListener> listeners = new HashMap<>();

    private String zkAddress;

    public ZkRegister(String cluster){
        this.zkAddress = cluster;
        client = new ZkClient(zkAddress,5000);
        this.pullData();
    }

    @Override
    public void pullData() {

        //第一次获取所有的子节点
        List<String> dubboNodes = client.getChildren(dubboRoot);

        processDubboNodes(dubboNodes);

        //处理新增或者删除的节点
        IZkChildListener listener = new IZkChildListener(){

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) {

                if(currentChilds == null || currentChilds.isEmpty()){
                    return;
                }

                logger.debug("dubbo目录下变更节点数量:"+currentChilds.size());
                processDubboNodes(currentChilds);
            }
        };

        client.subscribeChildChanges(dubboRoot,listener);
    }

    @Override
    public Map<String,Map<String, InterfaceMetaInfo>> getAllService() {
        return allProviders;
    }

    /**
     *
     * @param dubboNodes 路径是:/dubbo节点下的所以子节点
     */
    private void processDubboNodes(List<String> dubboNodes){

        logger.info("provider的数量:"+dubboNodes.size());

        for(String child : dubboNodes){

            String providerName = child;

            String childPath = dubboRoot + "/"+child+"/providers";

            //避免重复订阅
            if(!listeners.containsKey(childPath)){

                //添加变更监听
                IZkChildListener listener = new IZkChildListener(){

                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds){

                        if(currentChilds == null || currentChilds.isEmpty()){

                            return;
                        }

                        logger.debug("providers目录下变更节点数量:"+currentChilds.size());

                        processChildNodes(currentChilds);
                    }
                };

                listeners.put(childPath,listener);
            }

            List<String> children1 = client.getChildren(childPath);
            processChildNodes(children1);
        }

        for(Map.Entry<String,IZkChildListener> entry : listeners.entrySet()){

            client.subscribeChildChanges(entry.getKey(),entry.getValue());
        }
    }

    private void processChildNodes(List<String> children1) {

        //serviceName,serviceKey,provider的其他属性信息
        Map<String,Map<String, InterfaceMetaInfo>> tmp = new HashMap<>();

        for(String child1 : children1){

            try {
                child1 = URLDecoder.decode(child1,"utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("解析zk的dubbo注册失败:"+e);
            }

            URL dubboUrl = URL.valueOf(child1);

            String serviceName = dubboUrl.getParameter("application");
            String host = dubboUrl.getHost();
            int port = dubboUrl.getPort();
            String addr = host + ":" + port;

            String version = dubboUrl.getParameter("version","");

            String methods = dubboUrl.getParameter("methods");

            String group = dubboUrl.getParameter(Constants.GROUP_KEY,"default");

            String[] methodArray = methods.split(",");

            Set<String> methodSets = new HashSet<>();

            for(String mn : methodArray){

                methodSets.add(mn);
            }

            String providerName = dubboUrl.getParameter("interface","");

            if(providerName.isEmpty()){
                return;
            }

            String interfaceKey = BuildUtil.buildInterfaceKey(group,providerName,version);

            InterfaceMetaInfo metaItem = new InterfaceMetaInfo();

            metaItem.setInterfaceName(providerName);
            metaItem.setGroup(group);
            metaItem.setApplicationName(serviceName);
            metaItem.setMethodNames(methodSets);
            metaItem.setVersion(version);
            metaItem.setServiceAddr(child1);
            metaItem.getServerIps().add(addr);

            //替换策略
            if(tmp.containsKey(serviceName)){

                Map<String, InterfaceMetaInfo> oldMap = tmp.get(serviceName);

                //添加
                if(oldMap.containsKey(interfaceKey)){

                    InterfaceMetaInfo providerItemOld = oldMap.get(interfaceKey);
                    providerItemOld.getServerIps().add(addr);
                }else{
                    oldMap.put(interfaceKey,metaItem);
                }
            }else{

                Map<String, InterfaceMetaInfo> oldMap = new HashMap<>();
                oldMap.put(interfaceKey,metaItem);
                tmp.put(serviceName,oldMap);
            }
        }

        for(String serviceName : tmp.keySet()){

            if(allProviders.containsKey(serviceName)){

                Map<String, InterfaceMetaInfo> oldMap = allProviders.get(serviceName);
                Map<String, InterfaceMetaInfo> newMap = tmp.get(serviceName);

                //这里相当于替换和部分增加
                oldMap.putAll(newMap);

            }else{
                allProviders.put(serviceName,tmp.get(serviceName));
            }
        }
    }
}
