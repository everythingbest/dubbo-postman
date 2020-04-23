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

package com.rpcpostman.service.creation.entity;

import com.rpcpostman.service.GAV;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author everythingbest
 */
@Data
public class DubboPostmanService implements PostmanService {

    String cluster;

    String serviceName;

    GAV gav = new GAV();

    long generateTime;

    /**
     * 标识是否加载到classLoader
     * 这个值不能持久化
     */
    @JsonIgnore
    Boolean loadedToClassLoader = false;

    /**
     * 一个dubbo应用包含多个接口定义
     */
    List<InterfaceEntity> interfaceModels = new ArrayList<>();

    @Override
    public String getCluster() {
        return cluster;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public GAV getGav() {
        return gav;
    }

    @Override
    public List<InterfaceEntity> getInterfaceModelList() {
        return interfaceModels;
    }

    @Override
    public void setLoadedToClassLoader(boolean load) {

        this.loadedToClassLoader = load;
    }

    public boolean getLoadedToClassLoader(){
        return loadedToClassLoader;
    }
}
