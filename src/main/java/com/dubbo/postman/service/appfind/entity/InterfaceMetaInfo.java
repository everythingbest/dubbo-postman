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

package com.dubbo.postman.service.appfind.entity;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

/**
 * @author everythingbest
 * 接口及接口包含的方法及参数等元数据,从zk获取
 * 如果从其他地方拉取这个数据类型应该是兼容的
 */
@Data
public class InterfaceMetaInfo {
    
    String applicationName;
    
    String group;
    
    String version;
    
    String interfaceName;
    
    /**
     * 这个是zk拼接的完整地址:dubbo://192.....
     */
    String serviceAddr;
    
    Set<String> methodNames = Sets.newHashSet();
    
    Set<String> serverIps = Sets.newHashSet();
}
