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

package com.dubbo.postman.controller;

import com.dubbo.postman.dto.WebApiRspDto;
import com.dubbo.postman.service.dubboinvoke.GenericDubboConsumer;
import com.dubbo.postman.service.dubboinvoke.Request;
import com.dubbo.postman.util.Constant;
import com.dubbo.postman.util.IpUtil;
import com.dubbo.postman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author everythingbest
 * 访问dubbo的对外接口
 */
@Controller
public class DubboProxyController {

    private static final Logger logger = LoggerFactory.getLogger(DubboProxyController.class);

    @Resource
    GenericDubboConsumer genericDubboConsumer;

    @RequestMapping(value = "/dubbo", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto queryDubbo(@RequestParam(name = "zk") String zk,
                                   @RequestParam(name = "path") String path,
                                   @RequestParam(name = "content") String content,
                                   @RequestParam(name = "dubboIp") String dubboIp){

        Request request = new Request();

        request.setPath(path);

        request.getParams().put(Constant.ZK,zk);

        String realIp = "";
        
        if(dubboIp.contains(Constant.PORT_SPLITTER)){
        
            realIp = dubboIp.split(Constant.PORT_SPLITTER)[0];
        }
        
        if(!realIp.isEmpty() && IpUtil.isIp(realIp)){

            request.getParams().put(Constant.DUBBO_IP,dubboIp);
        }

        request.setBody(content);

        if(logger.isDebugEnabled()){

            logger.debug("接收DUBBO-POSTMAN请求:"+ JSON.objectToString(request));
        }

        return genericDubboConsumer.send(request);
    }
}
