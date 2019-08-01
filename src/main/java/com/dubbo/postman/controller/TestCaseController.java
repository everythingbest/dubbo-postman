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

import com.dubbo.postman.dto.UserCaseDto;
import com.dubbo.postman.dto.UserCaseGroupDto;
import com.dubbo.postman.dto.WebApiRspDto;
import com.dubbo.postman.repository.RedisRepository;
import com.dubbo.postman.util.RedisKeys;
import com.dubbo.postman.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用例相关的操作
 * 用例在这个系统里面指一个接口的请求,目前来说是对一个dubbo接口的请求{@link UserCaseDto}
 * @author everythingbest
 */
@Controller
@RequestMapping("/dubbo-postman/")
public class TestCaseController extends AbstractController{

    static Logger logger = LoggerFactory.getLogger(TestCaseController.class);

    @Autowired
    private RedisRepository cacheService;

    @RequestMapping(value = "case/save", method = RequestMethod.POST)
    @ResponseBody
    public WebApiRspDto<Boolean> saveCase(@RequestBody UserCaseDto caseDto){

        try {

            String groupName = caseDto.getGroupName();

            cacheService.setAdd(RedisKeys.CASE_KEY, groupName);

            String value = JSON.objectToString(caseDto);

            cacheService.mapPut(groupName, caseDto.getCaseName(), value);

            return WebApiRspDto.success(Boolean.TRUE);

        }catch (Exception exp){

            logger.error("保存测试case失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/group-case-detail/list", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<List<UserCaseDto>> getAllGroupCaseDetail(){

        List<UserCaseDto> groupDtoList = new ArrayList<>(1);

        try {

            Set<Object> groupNames = cacheService.members(RedisKeys.CASE_KEY);

            for (Object obj : groupNames) {

                Set<Object> caseNames = cacheService.mapGetKeys((String) obj);

                for(Object sub : caseNames){

                    String jsonStr = (String) cacheService.mapGet(obj.toString(), sub.toString());

                    UserCaseDto caseDto = JSON.parseObject(jsonStr, UserCaseDto.class);

                    groupDtoList.add(caseDto);

                }
            }

            return WebApiRspDto.success(groupDtoList);

        }catch (Exception exp){

            logger.error("查询所有用例详情失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/group/list", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<List<UserCaseGroupDto>> getAllGroupAndCaseName(){

        List<UserCaseGroupDto> groupDtoList = new ArrayList<>(1);

        try {

            Set<Object> groupNames = cacheService.members(RedisKeys.CASE_KEY);

            for (Object obj : groupNames) {

                UserCaseGroupDto parentDto = new UserCaseGroupDto();
                parentDto.setValue(obj.toString());
                parentDto.setLabel(obj.toString());

                Set<Object> caseNames = cacheService.mapGetKeys((String) obj);

                List<UserCaseGroupDto> children = new ArrayList<>(1);
                parentDto.setChildren(children);

                for(Object sub : caseNames){

                    UserCaseGroupDto dto = new UserCaseGroupDto();
                    dto.setValue(sub.toString());
                    dto.setLabel(sub.toString());
                    dto.setChildren(null);
                    children.add(dto);
                }

                groupDtoList.add(parentDto);
            }

            return WebApiRspDto.success(groupDtoList);

        }catch (Exception exp){

            logger.error("查询组和组内部case失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/group-name/list", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<List<String>> getAllGroupName(){

        List<String> groupDtoList = new ArrayList<>(1);

        try {

            Set<Object> groupNames = cacheService.members(RedisKeys.CASE_KEY);

            for (Object obj : groupNames) {

                UserCaseGroupDto groupDto = new UserCaseGroupDto();

                groupDto.setValue(obj.toString());

                groupDto.setLabel(obj.toString());

                groupDto.setChildren(null);

                groupDtoList.add(groupDto.getValue());
            }

            return WebApiRspDto.success(groupDtoList);

        }catch (Exception exp){

            logger.error("查询所有组名失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/detail", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<UserCaseDto> queryCaseDetail(@RequestParam(value = "groupName") String groupName,
                                                     @RequestParam(value = "caseName")String caseName){

        try {

            String jsonStr = (String) cacheService.mapGet(groupName, caseName);

            UserCaseDto caseDto = JSON.parseObject(jsonStr, UserCaseDto.class);

            if(caseDto.getClassName() == null){

                Map<String,String> classNameMap = getAllClassName(caseDto.getZkAddress(),caseDto.getServiceName());

                for(Map.Entry<String,String> item : classNameMap.entrySet()){

                    if(item.getValue().equals(caseDto.getProviderName())){

                        caseDto.setClassName(item.getKey());

                        break;
                    }
                }
            }

            return WebApiRspDto.success(caseDto);

        }catch (Exception exp){

            logger.error("查询case失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }

    @RequestMapping(value = "case/delete", method = RequestMethod.GET)
    @ResponseBody
    public WebApiRspDto<String> deleteDetail(@RequestParam(value = "groupName") String groupName,
                                             @RequestParam(value = "caseName")String caseName){

        try {

            cacheService.removeMap(groupName, caseName);

            return WebApiRspDto.success("删除成功");

        }catch (Exception exp){

            logger.error("查询case失败",exp);

            return WebApiRspDto.error(exp.getMessage());
        }
    }
}
