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
    getTemplate: (params) => {
        console.log("mock接收getTemplate",params);
        return {
            code: 0,
            error:'服务超时',
            data: {
                "arg0":"127.0.0.1：8080",
                "name":123
            }
        }
    },

    saveHisTemplate:(params) => {
        console.log("mock接收saveHisTemplate",params);
        return {
            code: 0,
            error:'服务超时',
            data: {"code":0,"name":"sgweg"}
        }
    },

    doRequest:(params) => {
        console.log("mock接收doRequest",params);
        return  {
            actualResponse:{"test":12134,"name":"sgweg"},
            testResponse:{"test":12134,"name":"sgweg","用例1":true,"用例2":false}
        }
    },
    getAllMethods:(params) => {
        console.log("mock接收getAllMethods",params);
        return {
            code: 0,
            error:'服务超时',
            data: [
                "method1"
            ]
        }
    },
    getAllProviders:(params)=>{
        console.log("mock接收getAllProviders",params);
        return {
            code: 0,
            error:'服务超时',
            data: {
                'abc':"a/sdg",
                'def':"a/sdg",
                'sgd':"a/sdg"
            }
        }
    },
    getRegisterService:(params)=>{
        console.log("mock接收getRegisterService",params);
        return {
            code: 0,
            error:'服务超时',
            data: ['abc','def','sgd']
        }
    },
}