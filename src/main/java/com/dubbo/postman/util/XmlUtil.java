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

package com.dubbo.postman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author everythingbest
 * pom.xml里面的dependency的解析
 */
public class XmlUtil {
    
    private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);
    
    public static Map<String,String> parseDependencyXml(String dependency){
    
        Map<String, String> dependencyMap = new HashMap<>();
        
        try {
    
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    
            byte[] bytes = dependency.getBytes();
    
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
    
            Document doc = dBuilder.parse(bi);
    
            doc.getDocumentElement().normalize();
    
            NodeList nList = doc.getElementsByTagName("groupId");
    
            String content = nList.item(0).getTextContent();
    
            dependencyMap.put("groupId",content.trim());
            
            nList = doc.getElementsByTagName("artifactId");
    
            content = nList.item(0).getTextContent();
    
            dependencyMap.put("artifactId",content.trim());
            
            nList = doc.getElementsByTagName("version");
    
            content = nList.item(0).getTextContent();
    
            dependencyMap.put("version",content.trim());
            
        }catch (Exception exp){
    
            logger.error("解析dependency失败,"+exp);
            
            return null;
        }
        
        return dependencyMap;
    }
}
