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

package com.rpcpostman.service.scenetest;

import com.rpcpostman.service.Pair;
import com.rpcpostman.service.invocation.Invocation;
import com.rpcpostman.service.invocation.Invoker;
import com.rpcpostman.service.invocation.entity.PostmanDubboRequest;
import com.rpcpostman.util.ExceptionHelper;
import com.rpcpostman.util.FileWithString;
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
class JSEngine {

    private final static String engineName = "JavaScript";

    private final static ScriptEngineManager manager = new ScriptEngineManager();
    private final static ScriptEngine engine = manager.getEngineByName(engineName);

    private static String internalJsFunction;

    protected static Map<String,Object> runScript(List<Pair<PostmanDubboRequest, Invocation>> requestList, Invoker<Object, PostmanDubboRequest> sender, String scriptCode){

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

    private static  String getAllJsContent(String[] pathArray){

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
