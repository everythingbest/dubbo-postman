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

package com.rpcpostman.config;

import com.rpcpostman.service.creation.entity.DubboPostmanService;
import com.rpcpostman.service.repository.redis.RedisRepository;
import com.rpcpostman.service.creation.entity.PostmanService;
import com.rpcpostman.service.context.InvokeContext;
import com.rpcpostman.service.registry.impl.DubboRegisterFactory;
import com.rpcpostman.service.repository.redis.RedisKeys;
import com.rpcpostman.util.FileWithString;
import com.rpcpostman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Set;

/**
 * @author everythingbest
 * 执行系统启动的时候需要加载的配置
 */
public class Initializer {

    private Logger logger = LoggerFactory.getLogger(Initializer.class);

    public void loadCreatedService(RedisRepository redisRepository,
                                   String dubboModelRedisKey){

        Set<Object> serviceKeys = redisRepository.mapGetKeys(dubboModelRedisKey);

        logger.info("已经创建的服务数量:" + serviceKeys.size());

        for (Object serviceKey : serviceKeys) {

            Object object = redisRepository.mapGet(dubboModelRedisKey, serviceKey);

            String dubboModelString = (String) object;

            PostmanService postmanService = JSON.parseObject(dubboModelString, DubboPostmanService.class);

            InvokeContext.putService((String)serviceKey,postmanService);
        }
    }

    void copySettingXml(String userHomePath) throws Exception {

        File file = new File(userHomePath+"/.m2/settings.xml");

        if(file.exists()){
            file.delete();
        }

        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        try {
            //复制文件内容
            changLocalRepository(userHomePath + "/" + ".m2");
        }catch (Exception exp){

            logger.error("复制setting.xml文件失败",exp);
            throw exp;
        }
    }

    /**
     * 把setting.xml文件里面的localRepository改成服务器上的绝对目录
     * @param newPath
     * @throws Exception
     */
    void changLocalRepository(String newPath) throws Exception{

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            byte[] bytes;

            URL url = this.getClass().getClassLoader().getResource("config/setting.xml");
            String content = FileWithString.file2String(url);
            bytes = content.getBytes();

            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);

            Document doc = dBuilder.parse(bi);

            doc.getDocumentElement().normalize();

            logger.info("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("localRepository");

            String oldText = nList.item(0).getTextContent();

            logger.info("setting.xml的localRepository旧值:"+oldText);

            String newContent = newPath+"/repository";

            nList.item(0).setTextContent(newContent);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);

            newPath = newPath + "/settings.xml";

            logger.info("setting.xml路径:"+newPath);

            StreamResult result = new StreamResult(new File(newPath));

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(source, result);

            logger.info("setting.xml 更新成功");

        } catch (Exception e) {

            logger.error("setting.xml 更新失败");

            throw e;
        }
    }

    void loadZkAddress(RedisRepository redisRepository){

        Set<Object> zkList = redisRepository.members(RedisKeys.CLUSTER_REDIS_KEY);

        if(zkList == null || zkList.isEmpty()){
            //系统第一次使用
            logger.warn("没有配置任何集群地址,请通过web页面添加集群地址");
        }

        if (zkList != null && !zkList.isEmpty()) {
            logger.info("系统当前已经添加的集群地址:" + zkList);
            for(Object cluster : zkList){
                DubboRegisterFactory.getInstance().addCluster((String) cluster);
                DubboRegisterFactory.getInstance().get((String) cluster);
            }
        }
    }
}
