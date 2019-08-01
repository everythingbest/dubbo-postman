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

export default {
    saveCase:(data)=>{
        console.log("mock接收saveCase",data);
        return {
            code: 0,
            error:'',
            data:true
        }
    },
    getGroupAndCaseName:(params)=>{
        console.log("mock接收getGroupAndCaseName",params);
        return {
            code: 0,
            error:'',
            data:[
                {
                    value: '通知服务',
                    label: '通知服务',
                    children: [{
                        value: '测试测试1',
                        label: '测试测试1',
                    }]
                },
                {
                    value: '城市服务',
                    label: '城市服务',
                    children: [{
                        value: '测试测试2',
                        label: '测试测试2',
                    }]
                }
            ]
        }
    },
    getAllGroupName:(params)=>{
        console.log("mock接收getALLGroupName",params);
        return {
            code: 0,
            error:'',
            data:["ABC","123"]
        }
    },
    queryCaseDetail:(params)=>{
        console.log("mock接收queryCaseDetail",params);
        return {
            code: 0,
            error:'',
            data:{
                caseName:'a',
                groupName:'test',
                zkAddress:'10.0.1.1:990',
                serviceName:'test-service',
                providerName:'provider-name',
                className:'provider-name',
                methodName:'method-name',
                requestValue:'{"sdgg":242,"nmae":"gwegt"}',
                testScript:'{"sdgg":242,"nmae":"gwegt"}'
            }
        }
    },
    deleteDetail:(params)=>{
        console.log("mock接收deleteDetail",params);
        return {
            code: 0,
            error:'',
            data:"ok"
        }
    },
    queryAllCaseDetail:(params)=>{
        console.log("mock接收queryAllCaseDetail",params);
        return {
            code: 0,
            error:'',
            data:[{
                caseName:'a',
                groupName:'test',
                zkAddress:'10.0.1.1:990',
                serviceName:'test-service',
                providerName:'provider-name',
                className:'provider-name',
                methodName:'method-name',
                requestValue:'{"sdgg":242,"nmae":"gwegt"}',
                responseValue:'{"sdgg":242,"nmae":"gwegt"}',
                testScript:"{\"sdgg\":242,\"nmae\":\"gwegt\"}"
            },
                {
                    caseName:'a1-testDto-testcaseb',
                    groupName:'test1-test2',
                    zkAddress:'10.0.1.1:990',
                    serviceName:'a1-testDto',
                    className:'ServiceDto',
                    providerName:'default/com/dubbo/postman/TestProvider/1/0/0',
                    methodName:'method-name244(sdgasd,sdgsg,gege,wegw,wegwgwe,wegwgw)',
                    responseValue:'{\n' +
                        '  "arg0":{\n' +
                        '\t"id":1,\n' +
                        '\t"name":"postman",\n' +
                        '\t"time":"2019-01-11 12:12:12"\n' +
                        '  }\n' +
                        '}',
                    requestValue:'{\n' +
                        '  "arg0":{\n' +
                        '\t"id":1,\n' +
                        '\t"name":"postman",\n' +
                        '\t"time":"2019-01-11 12:12:12"\n' +
                        '  }\n' +
                        '}',
                    testScript:"{\"sdgg\":242,\"nmae\":\"gwegt\"}"
                }]
        }
    },
}