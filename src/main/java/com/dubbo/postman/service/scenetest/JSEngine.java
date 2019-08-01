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

package com.dubbo.postman.service.scenetest;

import com.dubbo.postman.service.dubboinvoke.GenericDubboConsumer;
import com.dubbo.postman.service.dubboinvoke.Request;
import com.dubbo.postman.util.ExceptionHelper;
import com.dubbo.postman.util.FileWithString;
import org.apache.commons.lang.StringUtils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.URL;
import java.util.*;

/**
 * @author everythingbest
 * javaScript执行引擎
 */
public class JSEngine {

    final static String engineName = "JavaScript";

    final static ScriptEngineManager manager = new ScriptEngineManager();

    final static ScriptEngine engine = manager.getEngineByName(engineName);

    static String internalJsFunction;

    public static Map<String,Object> runScript(List<Request> requestList, GenericDubboConsumer sender, String scriptCode){


        if(StringUtils.isEmpty(internalJsFunction)){

            //"script/propertyOperation.js"
            //"script/sendWrapper.js"
            String[] pathArray = new String[]{"script/propertyOperation.js","script/sendWrapper.js"};

            internalJsFunction = getAllJsContent(pathArray);
        }

        //添加默认的js函数
        scriptCode = scriptCode+"\n"+internalJsFunction;

        Map<String,Object> map = new LinkedHashMap<>();

        try {

            Bindings bindings = engine.createBindings();
            bindings.put("reqs",requestList);
            bindings.put("sender",sender);
            bindings.put("rst",map);

            engine.eval(scriptCode,bindings);

            return map;

        } catch (Exception e) {

            String expResult = ExceptionHelper.getExceptionStackString(e);

            map.put("ScriptException",expResult);

            return map;
        }
    }

    static  String getAllJsContent(String[] pathArray){

        StringBuilder sb = new StringBuilder();

        for(String path : pathArray){

            URL url = JSEngine.class.getClassLoader().getResource(path);

            String content = FileWithString.file2String(url);

            sb.append(content);

            sb.append("\n");
        }

        return sb.toString();
    }
}
