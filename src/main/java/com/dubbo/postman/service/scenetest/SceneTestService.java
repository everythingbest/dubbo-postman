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

import com.dubbo.postman.dto.UserCaseDto;
import com.dubbo.postman.service.dubboinvoke.GenericDubboConsumer;
import com.dubbo.postman.service.dubboinvoke.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author everythingbest
 * 批量请求,用于关联测试的操作
 * 接口1传递参数给接口2
 *
 */
@Service
public class SceneTestService {

    private final static String TEST_SCRIPT = "script";

    @Autowired
    private GenericDubboConsumer gateWayServerClient;

    public Map<String,Object> process(List<Request> requestList, String sceneScript){

        Map<String,Object> rst = JSEngine.runScript(requestList,gateWayServerClient,sceneScript);

        return rst;
    }

    public List<Request> buildRequest(List<UserCaseDto> caseDtoList){

        List<Request> requestList = new ArrayList<>(1);

        for(UserCaseDto caseDto : caseDtoList){

            Request request = new Request();

            request.setBody(caseDto.getRequestValue());

            String path = "/"+caseDto.getServiceName()+"/"+caseDto.getProviderName()+"/"+caseDto.getMethodName();

            request.setPath(path);

            request.getParams().put("zk",caseDto.getZkAddress());

            if(caseDto.getTestScript() != null && !caseDto.getTestScript().isEmpty()){

                request.getParams().put(TEST_SCRIPT,caseDto.getTestScript());
            }

            requestList.add(request);
        }

        return requestList;
    }
}
