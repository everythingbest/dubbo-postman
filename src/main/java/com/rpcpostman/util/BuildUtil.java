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

package com.rpcpostman.util;

/**
 * @author everythingbest
 */
public class BuildUtil {

    private final static String splitter = "_";

    public static String buildServiceKey(String cluster, String serviceName){
        return cluster + splitter + serviceName;
    }

    public static String buildInterfaceKey(String group, String interfaceName, String version){

        String versionAppend = version;
        if(versionAppend == null || versionAppend.isEmpty()){
            versionAppend = Constant.DEFAULT_VERSION;
        }
        return group + splitter + interfaceName + splitter + versionAppend;
    }

    public static String getGroupByInterfaceKey(String interfaceKey){
        return interfaceKey.split(splitter)[0];
    }

    public static String getInterfaceNameByInterfaceKey(String interfaceKey){
        return interfaceKey.split(splitter)[1];
    }

    public static String getVersionByInterfaceKey(String interfaceKey){
        return interfaceKey.split(splitter)[2];
    }

    public static String getJavaMethodName(String methodName){
        return methodName.split("\\(")[0];
    }

    public static String buildMethodNameKey(String cluster,
                                            String serviceName,
                                            String group,
                                            String interfaceName,
                                            String version,
                                            String methodName){

        String serviceKey = BuildUtil.buildServiceKey(cluster, serviceName);
        String interfaceKey = BuildUtil.buildInterfaceKey(group,interfaceName,version);
        String key = serviceKey + splitter + interfaceKey + splitter + methodName;

        return key;
    }

    public static String getMethodNameKey(String cluster,
                                          String serviceName,
                                          String interfaceKey,
                                          String methodName){

        String serviceKey = BuildUtil.buildServiceKey(cluster, serviceName);
        String key = serviceKey + splitter + interfaceKey + splitter + methodName;

        return key;
    }

    /**
     * 把ip地址拼接成zk地址
     * zookeeper://192.168.11.29:2181?backup=192.168.11.32:2181,192.168.11.20:2181
     * @param zk
     * @return
     */
    public static String buildZkUrl(final String zk){
        String zkRegis = "";
        if(zk.contains(",")){
            String[] zs = zk.split(",");
            zkRegis = "zookeeper://"+zs[0];
            if(zs.length > 1){
                zkRegis += "?backup=";
                for(int index = 1; index<zs.length; index++){
                    zkRegis += zs[index];
                    if(index < zs.length-1){
                        zkRegis += ",";
                    }
                }
            }
        }else{
            zkRegis = "zookeeper://"+zk;
        }
        return zkRegis;
    }
}
