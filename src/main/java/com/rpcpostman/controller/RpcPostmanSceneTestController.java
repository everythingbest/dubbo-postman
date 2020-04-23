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

package com.rpcpostman.controller;

import com.rpcpostman.dto.SceneCaseDto;
import com.rpcpostman.dto.AbstractCaseDto;
import com.rpcpostman.dto.UserCaseDto;
import com.rpcpostman.dto.WebApiRspDto;
import com.rpcpostman.service.repository.redis.RedisRepository;
import com.rpcpostman.service.repository.redis.RedisKeys;
import com.rpcpostman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 场景相关的操作
 * 一个场景用例包含多个用例的请求响应,多个请求依赖前面的响应结果{@link SceneCaseDto}
 * @author everythingbest
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class RpcPostmanSceneTestController {

    static Logger logger = LoggerFactory.getLogger(RpcPostmanSceneTestController.class);

    @Autowired
    private RedisRepository cacheService;

    @RequestMapping(value = "case/scene/save", method = RequestMethod.POST)
    @ResponseBody
    public WebApiRspDto<Boolean> saveSceneCase(@RequestBody SceneCaseDto caseDto){

        try {

            String caseName = caseDto.getCaseName();
            String value = JSON.objectToString(caseDto);
            cacheService.mapPut(RedisKeys.SCENE_CASE_KEY,caseName,value);

            return WebApiRspDto.success(Boolean.TRUE);

        }catch (Exception exp){
            logger.error("保存场景失败",exp);
            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/scene/delete", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<Boolean> deleteSceneCase(@RequestParam String caseName){

        try {
            cacheService.removeMap(RedisKeys.SCENE_CASE_KEY,caseName);
            return WebApiRspDto.success(Boolean.TRUE);
        }catch (Exception exp){

            logger.error("删除场景测试失败",exp);
            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/scene/get", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<SceneCaseDto> getSceneCase(@RequestParam("caseName") String caseName){

        List<UserCaseDto> testCaseDtoList = new ArrayList<>(1);

        try{

            String value = (String)cacheService.mapGet(RedisKeys.SCENE_CASE_KEY,caseName);
            SceneCaseDto sceneCaseDto = JSON.parseObject(value, SceneCaseDto.class);
            List<UserCaseDto> identifyCaseDtos = sceneCaseDto.getCaseDtoList();

            for(AbstractCaseDto identifyCaseDto : identifyCaseDtos){

                String jsonStr = (String) cacheService.mapGet(identifyCaseDto.getGroupName(), identifyCaseDto.getCaseName());
                UserCaseDto caseDto = JSON.parseObject(jsonStr, UserCaseDto.class);
                testCaseDtoList.add(caseDto);
            }

            SceneCaseDto rspSceneCaseDto = new SceneCaseDto();
            rspSceneCaseDto.setCaseDtoList(testCaseDtoList);
            rspSceneCaseDto.setSceneScript(sceneCaseDto.getSceneScript());

            return WebApiRspDto.success(rspSceneCaseDto);

        }catch (Exception exp){

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/scene-name/list", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<Set<Object>> getAllSceneName(){

        try {

            Set<Object> groupNames = cacheService.mapGetKeys(RedisKeys.SCENE_CASE_KEY);
            return WebApiRspDto.success(groupNames);
        }catch (Exception exp){

            logger.error("查询所有场景测试失败",exp);
            return WebApiRspDto.error(exp.getMessage());
        }
    }
}
