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

package com.dubbo.postman.service.appfind.zk;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author everythingbest
 * 对于每一个zk地址构建一个对应的zkService用于处理这个zk下面节点变动的同步
 */
public class ZkServiceFactory {
    
    public static final Set<String> ZK_SET = new HashSet<>();

    /**
     * zk地址
     */
    public static final Map<String,ZkService> ZKSERVICE_MAP = new ConcurrentHashMap<>();
    
    public static synchronized ZkService get(String zkAddr){
    
        if(ZKSERVICE_MAP.containsKey(zkAddr)){
            
            return ZKSERVICE_MAP.get(zkAddr);
        }
        
        ZkService zkService = new ZkService(zkAddr);

        ZKSERVICE_MAP.put(zkAddr,zkService);

        return zkService;
    }
}
