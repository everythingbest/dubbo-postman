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

package com.dubbo.postman.config;

import com.dubbo.postman.repository.RedisRepository;
import com.dubbo.postman.service.appfind.zk.ZkServiceFactory;
import com.dubbo.postman.service.dubboinvoke.TemplateBuilder;
import com.dubbo.postman.domain.DubboModel;
import com.dubbo.postman.util.RedisKeys;
import com.dubbo.postman.util.FileWithString;
import com.dubbo.postman.util.JSON;
import com.dubbo.postman.util.CommonUtil;
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
                                   String dubboModelRedisKey,
                                   TemplateBuilder templateBuilder){

        Set<Object> keys = redisRepository.mapGetKeys(dubboModelRedisKey);

        logger.info("已经创建的服务数量:" + keys.size());

        for (Object key : keys) {

            String zk = CommonUtil.getZk((String) key);

            String serviceName = CommonUtil.getServiceName((String) key);

            Object object = redisRepository.mapGet(dubboModelRedisKey, key);

            String dubboModelString = (String) object;

            DubboModel dubboModel = JSON.parseObject(dubboModelString, DubboModel.class);

            dubboModel.setServiceName(serviceName);

            dubboModel.setZkAddress(zk);

            templateBuilder.buildTemplateByDubboModel(dubboModel);
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

        Set serverList = redisRepository.members(RedisKeys.ZK_REDIS_KEY);

        if(serverList == null || serverList.isEmpty()){

            //系统第一次使用
            logger.info("没有配置任何zk地址,请通过web-ui添加zk地址");
        }

        if (serverList != null && !serverList.isEmpty()) {

            ZkServiceFactory.ZK_SET.addAll(serverList);

            logger.info("系统当前已经添加的zk地址:" + ZkServiceFactory.ZK_SET);

            for(String zk : ZkServiceFactory.ZK_SET){

                ZkServiceFactory.get(zk);
            }
        }
    }
}
