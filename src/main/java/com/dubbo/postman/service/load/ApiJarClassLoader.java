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

package com.dubbo.postman.service.load;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author everythingbest
 * 加载api.jar,每个api.jar有一个ApiJarClassLoader加载
 * 这样可以方便进行重新加载和卸载
 * 这里使用了jdk默认的双亲委派加载机制
 * -------------------------------
 * 有一个特殊场景:
 * 用户发送请求到dubbo,从页面过来的数据时json,这时候需要把
 * 这个数据通过jackson反序列话成api.jar里面的dto实例
 * 有些dto里面用到了jackson里面的特殊注解,这时候在反序列话的
 * 时候是 父类加载器里面的类(jackson框架里面的反序列化类)需要识别子类里面的类(由ApiJarClassLoader加载,通常是jackson框架里面的注解),
 * 不然这些注解会解析失败导致数据反序列化不对,这时就需要破坏双亲委派的机制,使用Thread.currentThread().getContextClassLoader()来
 * 完成这个功能,参考{真正请求dubbo的地方}
 */
public class ApiJarClassLoader extends URLClassLoader {

    public ApiJarClassLoader(URL[] urls) {

        super(urls,Thread.currentThread().getContextClassLoader());
    }

    public Class<?> loadClassWithResolve(String name) throws ClassNotFoundException {

        return loadClass(name, true);
    }

    public void appendURL(URL url){

        addURL(url);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        return super.loadClass(name, resolve);
    }
}
