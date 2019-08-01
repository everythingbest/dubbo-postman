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

package com.dubbo.postman.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author everythingbest
 * 一个template代表一个唯一的方法访问路径
 */
@Data
public class RequestTemplate implements Cloneable,Serializable{
    
    private static final long serialVersionUID = 1L;

    private boolean useDubbo = false;

    private String path;
    
    private String registry;
    
    private String dubboUrl;

    private String serviceName;

    private String interfaceName;
    
    private String version;
    
    private String methodName;
    
    private String group;

    private int retries = 0;

    private List<RequestParam> matcherParams = new ArrayList<>();

    @JsonIgnore
    private boolean hasRunTimeInfo = false;

    /**
     * 通过MatcherParam生成
     */
    private List<String> paramTypes = new ArrayList<>();

    private List<Object> paramValues = new ArrayList<>();

    @Override
    public Object clone(){

        RequestTemplate template = null;

        try {

            template = (RequestTemplate) super.clone();

            //matcherParams 不需要修改
            template.setUseDubbo(false);

            //这里要重置,每次请求设置新的值
            template.setParamTypes(new ArrayList<>());

            template.setParamValues(new ArrayList<>());

        }catch (CloneNotSupportedException e){
        }

        return template;
    }
}
