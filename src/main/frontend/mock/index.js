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

import Mock from 'mockjs'

import accessApi from './access'
import createApi from './create'
import commonApi from './common'
import configApi from './config'
import testCase from './testCase'

import caseRun from './caseRun'

import associationCase from './associationCase'

Mock.XHR.prototype.proxy_send = Mock.XHR.prototype.send
Mock.XHR.prototype.send = function() {
    if (this.custom.xhr) {
        this.custom.xhr.withCredentials = this.withCredentials || false
    }
    this.proxy_send(...arguments)
}

// Mock.setup({
//   timeout: '350-600'
// })

//服务访问相关
Mock.mock(/dubbo-postman\/result\/interface\/method\/param/, 'get', accessApi.getTemplate);
Mock.mock(/dubbo-postman\/result\/template\/save/, 'get', accessApi.saveHisTemplate);
Mock.mock(/dubbo/, 'get', accessApi.doRequest);
Mock.mock(/dubbo-postman\/result\/interfaceNames/, 'get', accessApi.getAllProviders);
Mock.mock(/dubbo-postman\/result\/interface/, 'get', accessApi.getAllMethods);
Mock.mock(/dubbo-postman\/result\/serviceNames/, 'get', accessApi.getRegisterService);

//服务创建相关
Mock.mock(/dubbo-postman\/create/, 'get', createApi.upload);
Mock.mock(/dubbo-postman\/refresh/, 'get', createApi.refresh);
Mock.mock(/dubbo-postman\/result\/appNames/, 'get', createApi.getZkServices);

//共功模块
Mock.mock(/dubbo-postman\/all-zk/, 'get', commonApi.getAllZk);
//config模块
Mock.mock(/dubbo-postman\/configs/, 'get', configApi.configs);
Mock.mock(/dubbo-postman\/zk\/del/, 'get', configApi.deleteZk);

//测试case相关
Mock.mock(/dubbo-postman\/case\/save/, 'post', testCase.saveCase);
Mock.mock(/dubbo-postman\/case\/group\/list/, 'get', testCase.getGroupAndCaseName);
Mock.mock(/dubbo-postman\/case\/group-name\/list/, 'get', testCase.getAllGroupName);
Mock.mock(/dubbo-postman\/case\/group-case-detail\/list/, 'get', testCase.queryAllCaseDetail);
Mock.mock(/dubbo-postman\/case\/detail/, 'get', testCase.queryCaseDetail);
Mock.mock(/dubbo-postman\/case\/delete/, 'get', testCase.deleteDetail);

//用例运行相关
Mock.mock(/dubbo-postman\/case\/multiple\/run/, 'post', caseRun.batchCaseRun);

//关联用例相关
Mock.mock(/dubbo-postman\/case\/association\/save/, 'post', associationCase.saveAssociationCase);
Mock.mock(/dubbo-postman\/case\/association-name\/list/, 'get', associationCase.getAllAssociationName);
Mock.mock(/dubbo-postman\/case\/association\/get/, 'get', associationCase.getAssociationCase);
Mock.mock(/dubbo-postman\/case\/association\/delete/, 'get', associationCase.deleteAssociationCase);

export default Mock
