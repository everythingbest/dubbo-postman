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

package com.dubbo.postman.repository;

import com.dubbo.postman.domain.RequestTemplate;
import com.dubbo.postman.domain.DubboModel;
import com.dubbo.postman.util.CommonUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author everythingbest
 *
 */
public class LocalStore {

    private static final Map<String, RequestTemplate> CACHED_TEMPLATES = new ConcurrentHashMap<>();

    public static final Map<String, DubboModel> DUBBO_MODEL_MAP = new ConcurrentHashMap<>();

    /**
     * @param path
     * @return
     */
    public static RequestTemplate get(String path){
        
        RequestTemplate template = CACHED_TEMPLATES.getOrDefault(path,null);
        
        return template;
    }

    /**
     *
     * @param requestPath
     * @param template
     * @param model
     */
    public static void put(String requestPath, RequestTemplate template, DubboModel model){
       
        CACHED_TEMPLATES.put(requestPath,template);

        DUBBO_MODEL_MAP.put(requestPath,model);

        String modelKey = CommonUtil.getDubboModelKey(model.getZkAddress(), model.getServiceName());

        DUBBO_MODEL_MAP.put(modelKey,model);
    }
}
