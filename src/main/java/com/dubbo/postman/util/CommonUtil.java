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

/**
 * @author everythingbest
 */
public class CommonUtil {
    
    public static String getDubboModelKey(String zk, String serviceName){
        
        return zk + "_"+serviceName;
    }
    
    public static String getZk(String modelKey){
        
        return modelKey.split("_")[0];
    }
    
    public static String getServiceName(String modelKey){
        
        return modelKey.split("_")[1];
    }

    /**
     * 把ip地址拼接成zk地址
     * zookeeper://192.168.11.29:2181?backup=192.168.11.32:2181,192.168.11.20:2181
     * @param zk
     * @return
     */
    public static String buildZkUrl(String zk){
        
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
