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

package com.dubbo.postman.service.maven;

import com.dubbo.postman.util.LogResultPrintStream;
import org.apache.maven.cli.MavenCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author everythingbest
 * 处理从nexus下载api.jar
 * 然后通过embededmaven下载这个api.jar的依赖
 */
public class MavenProcessor {

    private Logger logger = LoggerFactory.getLogger(MavenProcessor.class);

    private String nexusUrl;

    private String fileBasePath;

    public MavenProcessor(String nexusUrl, String fileBasePath) {

        this.nexusUrl = nexusUrl;

        this.fileBasePath = fileBasePath;
    }

    public boolean process(String serviceDirName,
                           String groupId,
                           String artifactId,
                           String version,
                           LogResultPrintStream resultPrintStream){
    
        String pomPath = downPomAndJar(serviceDirName,groupId,artifactId,version,resultPrintStream);

        logger.info("构建完成30%...");

        if(pomPath == null){

            return false;
        }

        return mavenCopyDependencies(pomPath,resultPrintStream);
    }
    
    private String downPomAndJar(String serviceName, String groupId, String artifactId, String version, LogResultPrintStream resultPrintStream){

        resultPrintStream.println("准备下载api.jar文件...");
        resultPrintStream.println("groupId:"+groupId);
        resultPrintStream.println("artifactId:"+artifactId);
        resultPrintStream.println("version:"+version);

        String jarUrl = buildJarUrl(groupId,artifactId,version);
        resultPrintStream.println("api.jar的url:"+jarUrl);

        String pomUrl = buildPomUrl(groupId,artifactId,version);
        resultPrintStream.println("构建api.jar的pom.xml文件的url:"+pomUrl);

        String basePath = fileBasePath+"/"+serviceName;
        
        File file = new File(basePath);
        
        if(file.exists()){
            
            file.delete();
        }
    
        file.mkdir();
        
        String jarPath = fileBasePath+"/"+serviceName+"/lib";
    
        File libfile = new File(jarPath);
    
        if(libfile.exists()){
        
            file.delete();
        }
    
        libfile.mkdir();

        resultPrintStream.println("api.jar下载路径:"+jarPath);

        String pomPath = basePath;

        resultPrintStream.println("pom.xml的下载路径:"+pomPath);

        try {

            resultPrintStream.println("开始下载:"+artifactId + ".jar文件");

            doDownLoadFile(jarUrl, jarPath, artifactId + ".jar");

            resultPrintStream.println("下载:"+artifactId + ".jar文件成功");

            resultPrintStream.println("开始下载:"+artifactId + "的pom.xml文件");

            doDownLoadFile(pomUrl, pomPath, "pom.xml");

            resultPrintStream.println("下载:"+artifactId + "的pom.xml文件成功");

        }catch (IOException exp){

            resultPrintStream.println("[ERROR]下载pom.xml或api.jar文件失败:"+exp.getMessage());
            logger.warn("下载pom或jar失败:",exp);
            
            return null;
        }
        
        return pomPath;
    }
    
    boolean mavenCopyDependencies(String pomPath, LogResultPrintStream resultPrintStream){
    
        MavenCli cli = new MavenCli();

        resultPrintStream.println("处理api.jar的所有依赖,通过执行maven命令: 'mvn dependency:copy-dependencies"
                + " -DoutputDirectory=./lib -DexcludeScope=provided -U'");

        resultPrintStream.println("开发执行maven命令");

        System.setProperty("maven.multiModuleProjectDirectory","./");

        int result = cli.doMain(new String[]{
                                              "dependency:copy-dependencies",
                                              "-DoutputDirectory=./lib",
                                              "-DexcludeScope=provided ",
                                              "-U"}, pomPath, resultPrintStream, resultPrintStream);


        boolean success = (result == 0);

        logger.info("构建完成100%,构建结果:{}",success);

        resultPrintStream.setSuccess(success);

        if (success) {

            resultPrintStream.println("maven执行成功,文件路径:"+pomPath);

            return true;
        } else {

            resultPrintStream.println("maven执行失败,文件路径:"+pomPath);
            logger.warn("maven执行失败,文件路径:"+pomPath);
    
            return false;
        }
    }
    
    void doDownLoadFile(String baseUrl,String filePath,String fileName) throws IOException{
    
        URL httpUrl=new URL(baseUrl);
    
        HttpURLConnection conn=(HttpURLConnection) httpUrl.openConnection();
    
        conn.setDoInput(true);
    
        conn.setDoOutput(true);
    
        conn.connect();
    
        InputStream inputStream = conn.getInputStream();
    
        BufferedInputStream bis = new BufferedInputStream(inputStream);
    
        //判断文件的保存路径后面是否以/结尾
        if (!filePath.endsWith("/")) {
        
            filePath += "/";
        
        }
    
        FileOutputStream fileOut = new FileOutputStream(filePath+fileName);
    
        BufferedOutputStream bos = new BufferedOutputStream(fileOut);
    
        byte[] buf = new byte[4096];
        int length = bis.read(buf);
        //保存文件
        while(length != -1)
        {
            bos.write(buf, 0, length);
            length = bis.read(buf);
        }
        
        bos.close();
        bis.close();
        conn.disconnect();
    }
    
    String buildJarUrl(String groupId,String artifactId,String version){
        
        String upperV = version.trim().toUpperCase();
        
        String suffixUrl;
        
        if(upperV.endsWith("SNAPSHOT")){
        
            suffixUrl = "?r="+"snapshots&g="+groupId+"&a="+artifactId+"&v="+version+"&e=jar";
        
        }else{
        
            suffixUrl = "?r="+"releases&g="+groupId+"&a="+artifactId+"&v="+version+"&e=jar";
        }
        
        return nexusUrl+suffixUrl;
    }
    
    String buildPomUrl(String groupId,String artifactId,String version){
        
        String upperV = version.trim().toUpperCase();
    
        String suffixUrl;
    
        if(upperV.endsWith("SNAPSHOT")){
        
            suffixUrl = "?r="+"snapshots&g="+groupId+"&a="+artifactId+"&v="+version+"&e=pom";
        
        }else{
        
            suffixUrl = "?r="+"releases&g="+groupId+"&a="+artifactId+"&v="+version+"&e=pom";
        }
    
        return nexusUrl+suffixUrl;
    }
}
