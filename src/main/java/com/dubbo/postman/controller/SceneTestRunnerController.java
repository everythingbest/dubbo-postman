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

import com.dubbo.postman.dto.SceneCaseDto;
import com.dubbo.postman.dto.AbstractCaseDto;
import com.dubbo.postman.dto.UserCaseDto;
import com.dubbo.postman.dto.WebApiRspDto;
import com.dubbo.postman.repository.RedisRepository;
import com.dubbo.postman.service.dubboinvoke.Request;
import com.dubbo.postman.service.scenetest.SceneTestService;
import com.dubbo.postman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 批量处理用例
 * 场景测试运行相关的操作
 * @author everythingbest
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class SceneTestRunnerController {

    private static Logger logger = LoggerFactory.getLogger(SceneTestRunnerController.class);

    @Resource
    RedisRepository cacheService;

    @Autowired
    SceneTestService requestService;

    @ResponseBody
    @RequestMapping(value = "case/scene/run", method = RequestMethod.POST)
    public WebApiRspDto<Map<String,Object>> runSceneCase(@RequestBody SceneCaseDto sceneDto){

        try {

            List<UserCaseDto> testCaseDtoList = new ArrayList<>(1);

            for (AbstractCaseDto dto : sceneDto.getCaseDtoList()) {

                String jsonStr = (String) cacheService.mapGet(dto.getGroupName(), dto.getCaseName());

                UserCaseDto caseDto = JSON.parseObject(jsonStr, UserCaseDto.class);

                testCaseDtoList.add(caseDto);
            }

            List<Request> requestList = requestService.buildRequest(testCaseDtoList);

            Map<String,Object> rst = requestService.process(requestList,sceneDto.getSceneScript());

            return WebApiRspDto.success(rst);

        }catch (Exception exp){

            logger.error("调用异常,",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }
}
