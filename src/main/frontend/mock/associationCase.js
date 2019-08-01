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
    saveAssociationCase:(data)=>{
        console.log("mock接收saveAssociationCase",data);
        return {
            code: 0,
            error:'',
            data:true
        }
    },
    getAllAssociationName:(params)=>{
        console.log("mock接收getAllAssociationName",params);
        return {
            code: 0,
            error:'',
            data:["ABC","123"]
        }
    },
    getAssociationCase:(params)=>{
        console.log("mock接收getAssociationCase",params);
        return {
            code: 0,
            error:'',
            data:{
                sceneScript:'var content = reqs[0].body;\n' +
                    'rst.put("content",content);\n' +
                    'var requestObj = JSON.parse(content);\n' +
                    'requestObj.type = 1;\n' +
                    'reqs[0].body = JSON.stringify(requestObj);\n' +
                    'var result = sender.send(reqs[0]);\n' +
                    'var obj = JSON.parse(result);\n' +
                    'var code = obj.data;\n' +
                    'rst.put("result",result);\n' +
                    'rst.put("code",code);',
                caseDtoList:[{
                    caseName:'a',
                    groupName:'test',
                    zkAddress:'10.0.1.1:990',
                    serviceName:'test-service',
                    providerName:'provider-name',
                    className:'provider-name',
                    methodName:'method-name',
                    requestValue:'{"sdgg":242,"nmae":"gwegt"}',
                    testScript:'{"sdgg":242,"nmae":"gwegt"}'
                }]
            }

        }
    },
    deleteAssociationCase:(params)=>{
        console.log("mock接收deleteAssociationCase",params);
        return {
            code: 0,
            error:'',
            data:"ok"
        }
    },
}