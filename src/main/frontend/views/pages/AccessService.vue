<!--
  - MIT License
  -
  - Copyright (c) 2019 everythingbest
  -
  - Permission is hereby granted, free of charge, to any person obtaining a copy
  - of this software and associated documentation files (the "Software"), to deal
  - in the Software without restriction, including without limitation the rights
  - to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  - copies of the Software, and to permit persons to whom the Software is
  - furnished to do so, subject to the following conditions:
  -
  - The above copyright notice and this permission notice shall be included in all
  - copies or substantial portions of the Software.
  -
  - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  - IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  - FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  - AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  - LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  - OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  - SOFTWARE.
  -->

<template>
    <section class="app-container">
    <el-form label-width="90px"
             @submit.prevent="onSubmit"
             style="margin:0px;width:100%;min-width:600px;">
        <el-row v-show=pageArray[pageIndex].zkServiceShow>
            <el-col :span="14">
                <el-form-item label="注册中心：" >
                    <el-select v-model="pageArray[pageIndex].zk" placeholder="必填，访问的ZK地址" filterable @change="changeZk">
                        <el-option v-for="option in pageArray[pageIndex].zkList" v-bind:value="option" :label="option">
                            {{ option }}
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <el-col :span="6">
                <el-form-item label="服务名称：">
                    <el-select v-model="pageArray[pageIndex].serviceName" style="width: 100%" placeholder="必填，访问的服务名称" filterable @change="changeService">
                        <el-option v-for="option in pageArray[pageIndex].serviceNames" v-bind:value="option" :label="option">
                            {{ option }}
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <el-col :span="2">
                <el-form-item label-width="0px">
                    <el-button class="my-button" type="info" plain icon="el-icon-refresh" style="width: 100%" v-on:click="refreshService">刷新服务</el-button>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row>
            <el-col :span="5">
                <el-form-item label="接口名称：">
                    <el-select v-model="pageArray[pageIndex].provider" placeholder="必填，请选择接口名称" filterable @change="changeProvider">
                        <el-option v-for="option in pageArray[pageIndex].providers" v-bind:value="option" :label="option">
                            {{ option }}
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <el-col :span="9">
                <el-form-item label="方法：" label-width="60px">
                    <el-select v-model="pageArray[pageIndex].methodName" placeholder="必填，请选择方法名称" filterable @change="changeMethodName">
                        <el-option v-for="option in pageArray[pageIndex].methodNames" v-bind:value="option.name" :label="option.name">
                            {{ option.name }}
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <el-col :span="7">
                <el-form-item label="测试用例：">
                    <el-cascader
                            placeholder="可选，使用保存的用例名称"
                            v-model="pageArray[pageIndex].groupNames"
                            expand-trigger="hover"
                            filterable
                            :options="pageArray[pageIndex].groupWithCase"
                            @change="changeTestCase">
                    </el-cascader>
                </el-form-item>
            </el-col>

            <el-col :span="1">
                <el-form-item label-width="0px">
                    <el-button class="cpLink my-button" plain type="info" v-clipboard:error="onError"
                               v-clipboard:copy="pageArray[pageIndex].caseName"  v-clipboard:success="onCopy">
                        复制
                    </el-button>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row >
            <el-col :span="5">
                <el-form-item label="实例IP：" label-width="90px">
                    <el-select v-model="pageArray[pageIndex].ip" placeholder="可选，调试的时候使用" filterable clearable>
                        <el-option v-for="option in pageArray[pageIndex].ips" v-bind:value="option" :label="option">
                            {{ option }}
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <el-col :span="1" :offset="1">
                <el-form-item label-position="left" label-width="0px">
                    <el-button :loading="pageArray[this.pageIndex].isSending" type="primary" v-on:click="send">Send</el-button>
                </el-form-item>
            </el-col>

            <el-col :span="5" :offset="1">
                <el-form-item >
                    <el-dropdown  @command="handleCommand"  split-button @click="saveNewTemplate" type="info">
                        保存
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="saveAs">另存为...</el-dropdown-item>
                            <el-dropdown-item command="zkServiceShow">
                                {{pageArray[pageIndex].zkServiceShow == true ?
                                pageArray[pageIndex].zkServiceHideName : pageArray[pageIndex].zkServiceShowName}}
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row>
            <el-col :span="22">
                <el-form-item label-width="60px">
                    <el-tabs v-model="pageActiveName"  @tab-click="tabSwitch" style="height: 16px;">
                        <el-tab-pane v-for="item in tabMapOptions" :key="item.index" :label="item.name" :name="item.name">
                            <!--<keep-alive>
                                <tab-pane v-if="pageActiveName==item.index" :type="item.name"/>
                            </keep-alive>-->
                        </el-tab-pane>
                    </el-tabs>
                </el-form-item>
            </el-col>
        </el-row>

        <br/>
        <el-row v-show="pageArray[pageIndex].requestResponseShow">
            <el-col :span="22" >
                <el-alert
                        title="请求内容："
                        type="info"
                        :closable="false">
                    <el-button type="info" size="mini" v-on:click="format">格式化</el-button>
                    <el-tag  type="info">F11全屏</el-tag>
                    <el-tag  type="info">Ctrl-F搜索</el-tag>
                    <el-tag  type="info">Ctrl-G跳转到下一个搜索目标</el-tag>
                    <el-tag  type="info">Alt-G跳转到指定行</el-tag>
                </el-alert>

                <div class="codemirror" style="height:auto" >
                    <codemirror ref="myCm"
                                v-model="pageArray[pageIndex].request"
                                :options="cmOptions"
                                >
                    </codemirror>
                </div>
            </el-col>
        </el-row>

        <el-row v-show="pageArray[pageIndex].requestResponseShow">
            <el-col :span="22" >
                <el-alert
                        title="响应结果："
                        type="info"
                        :closable="false">
                </el-alert>
                <div class="codemirror">
                    <codemirror ref="myCm1"
                                v-model="pageArray[pageIndex].response"
                                :options="readOnlycmOptions">
                    </codemirror>
                </div>
            </el-col>
        </el-row>

        <el-dialog title="保存到测试用例" :visible.sync="pageArray[pageIndex].dialogFormVisible">
            <el-form-item label="用例组名称：" label-width="100px">
                <el-select
                        v-model="pageArray[pageIndex].groupName"
                        filterable
                        allow-create
                        placeholder="请选择或输入组名称">
                    <el-option
                            v-for="item in pageArray[pageIndex].groupOnlyNames"
                            :key="item"
                            :label="item"
                            :value="item">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="用例名称：" label-width="100px">
                <el-input v-model="pageArray[pageIndex].caseName" auto-complete="off"></el-input>
            </el-form-item>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="pageArray[pageIndex].dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveNewTemplate">确 定</el-button>
                </div>
        </el-dialog>

        <el-tooltip placement="top" content="返回顶部">
            <back-to-top :custom-style="myBackToTopStyle" :visibility-height="300" :back-position="50" transition-name="fade"/>
        </el-tooltip>
    </el-form>
    </section>
</template>

<script>

    import '../../utils/formatting';
    import {getAllZk} from '@/api/common';
    import { refresh} from '@/api/create';
    import {getRegisterService,getAllMethods,getArgs,getAllProviders,
        getTemplate,getRemoteHistoryTemplate,getRemoteAssignedTemplate,
        doRequest,saveHisTemplate} from '@/api/access';
    import{saveCase,getGroupAndCaseName,getAllGroupName,queryCaseDetail} from '@/api/testCase';
    import BackToTop from '@/components/BackToTop'

    export default {
        name: 'accessService',
        components: { BackToTop },
        data() {
            return{
                cachePageName:"allPages",

                myBackToTopStyle: {
                    right: '50px',
                    bottom: '50px',
                    width: '40px',
                    height: '40px',
                    'border-radius': '4px',
                    'line-height': '45px', // 请保持与高度一致以垂直居中 Please keep consistent with height to center vertically
                    background: '#e7eaf1'// 按钮的背景颜色 The background color of the button
                },

                tabMapOptions:[
                    {
                        name:'查询界面A',
                        index:0,
                    },
                    {
                        name:'查询界面B',
                        index:1,
                    },{
                        name:'查询界面C',
                        index:2,
                    },{
                        name:'查询界面1',
                        index:3,
                    },
                    {
                        name:'查询界面2',
                        index:4,
                    },{
                        name:'查询界面3',
                        index:5,
                    }
                ],
                pageActiveName:'查询界面A',
                pageArray:[],
                pageIndex:0,

                pageItem:{

                    isSending:false,

                    testScriptShow:true,
                    testScriptShowName:'显示测试脚本',
                    testScriptHideName:'隐藏测试脚本',

                    requestResponseShow:true,
                    requestResponseShowName:'显示请求响应窗口',
                    requestResponseHideName:'隐藏请求响应窗口',

                    zkServiceShow:true,
                    zkServiceShowName:'显示服务名称',
                    zkServiceHideName:'隐藏服务名称',

                    autoTriggerWatch:true,
                    zk:'',
                    zkList:[],

                    serviceName:'',
                    serviceNames:[],

                    methodNames:[],
                    methodName:'',

                    dialogFormVisible:false,
                    groupOnlyNames:['dsg','abc'],
                    groupName:'',
                    caseName:'',

                    providerNameMap:{},
                    providers:[],
                    //是interfaceKey
                    provider:"",
                    //暂时不能解决同步加上这个
                    providerName:'',

                    ips:[],
                    ip:'',

                    groupWithCase:[],
                    groupNames:[],
                    request: '',
                    response:'',

                },
                readOnlycmOptions: {

                    readOnly:true,
                    // codemirror options
                    mode: 'application/json',

                    styleActiveLine: false,
                    lineNumbers: true,

                    line: true,

                    lint: true,
                    foldGutter: true,
                    lineWrapping:true,
                    gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter","CodeMirror-lint-markers"],

                    smartIndent:true,
                    indentWithTabs: true,
                    // autofocus: true,
                    matchBrackets: true,
                    extraKeys: {
                        "F11": function (cm) {
                            cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                        },
                        "Esc": function (cm) {
                            if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                        },
                    },
                    theme: "eclipse"//monokai eclipse zenburn
                },
                cmOptions: {
                    // codemirror options
                    mode: 'application/json',

                    styleActiveLine: false,
                    lineNumbers: true,

                    line: true,

                    lint: true,
                    foldGutter: true,
                    lineWrapping:true,
                    gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter","CodeMirror-lint-markers"],

                    smartIndent:true,
                    indentWithTabs: true,
                    // autofocus: true,
                    matchBrackets: true,
                    extraKeys: {
                        "F11": function (cm) {
                            cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                        },
                        "Esc": function (cm) {
                            if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                        },
                    },
                    theme: "eclipse"//monokai eclipse zenburn
                }
            }
        },
        methods: {
            handleCommand(command) {
                if(command == 'saveAs'){
                    this.pageArray[this.pageIndex].dialogFormVisible = true;
                }else if(command == 'zkServiceShow'){
                    this.pageArray[this.pageIndex].zkServiceShow = !this.pageArray[this.pageIndex].zkServiceShow
                }
            },
            booleanValue(value){
                console.log("是否布尔:",value);
                if(typeof(value) == "boolean"){
                    return true;
                }
                return false;
            },
            refreshService(){
                if(!this.pageArray[this.pageIndex].zk ||
                    !this.pageArray[this.pageIndex].serviceName){
                    this.$message.error('必须选择:zk和serviceName');
                    return;
                }
                let encodedZk = encodeURI(this.pageArray[this.pageIndex].zk);

                let params = {
                    "zk":encodedZk,
                    "zkServiceName":this.pageArray[this.pageIndex].serviceName
                };

                const loading = this.$loading({
                    lock: true,
                    text: '正在刷新服务中,需要下载依赖的jar比较耗时,请耐心等待......',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });

                refresh(params).then(res => {
                    if (res.status == 200) {

                        if(res.data.code == 0){
                            this.$message.success({
                                'message': '刷新成功!',
                                'type': 'success'
                            });
                            //刷新页面
                            setTimeout(() => {
                                window.location.reload();
                            }, 500);

                        }else{
                            this.$message.error({
                                'message': res.data.error+",请重试或联系管理员",
                                'type': 'fail'
                            })
                        }
                    }else{
                        this.$message.error({
                            'message': '系统错误,请重试或联系管理员',
                            'type': 'fail'
                        })
                    }
                }).finally(()=>{
                    setTimeout(() => {
                        loading.close();
                    }, 100);
                });
            },
            tabSwitch(tab, event){
                console.log("当前元素:",tab.index);
                this.pageIndex = tab.index;
                this.queryGroupWithCase();
                this.queryAllGroupOnlyNames();
            },
            changeTestCase(value){
                let groupName = value[0];
                let caseName = value[1];
                let param = {
                    groupName:groupName,
                    caseName:caseName
                };
                queryCaseDetail(param).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.pageArray[this.pageIndex].autoTriggerWatch = false;
                        let data = res.data.data;
                        this.pageArray[this.pageIndex].zk = data.zkAddress;
                        this.pageArray[this.pageIndex].serviceName = data.serviceName;

                        this.pageArray[this.pageIndex].provider = data.className;

                        this.pageArray[this.pageIndex].providerName = data.providerName;

                        this.pageArray[this.pageIndex].methodName = data.methodName;
                        this.pageArray[this.pageIndex].request = data.requestValue;
                        this.pageArray[this.pageIndex].response = '';

                        this.pageArray[this.pageIndex].groupName = data.groupName;
                        this.pageArray[this.pageIndex].caseName = data.caseName;
                        this.formatContent();
                    }else{
                        this.$message.error("保存失败");
                        console.log("查询caseDetail失败,",res.data.error);
                    }
                }).finally(() => {
                    this.pageArray[this.pageIndex].autoTriggerWatch = true;
                    this.getProviders();
                    //手动触发更新列表
                    this. getAllService();
                    this.getMethods();
                });
            },
            format(){
                this.formatContent();
            },
            formatContent(){
                var totalLines = this.codemirror.lineCount();
                this.codemirror.autoFormatRange(
                    {line:0, ch:0},
                    {line:totalLines},this.pageArray[this.pageIndex].request);
            },
            formatContent1(){
                var totalLines = this.codemirror1.lineCount();
                this.codemirror1.autoFormatRange({line:0, ch:0},
                    {line:totalLines},
                    this.pageArray[this.pageIndex].response);
            },
            send(){
                if(!this.pageArray[this.pageIndex].zk ||
                    !this.pageArray[this.pageIndex].serviceName ||
                    !this.pageArray[this.pageIndex].provider ||
                    !this.pageArray[this.pageIndex].methodName){

                    this.$message(
                        {
                            message: "必须选择一个方法进行访问!",
                            type: 'error',
                            duration: 1 * 1000
                        }
                    );
                    return;
                }
                let ezk = encodeURI(this.pageArray[this.pageIndex].zk);
                let eip = encodeURI(this.pageArray[this.pageIndex].ip);

                let providerName = this.pageArray[this.pageIndex].provider;
                let infKey = this.pageArray[this.pageIndex].providerNameMap[providerName];

                let path = "/"+this.pageArray[this.pageIndex].serviceName +"/"+
                    encodeURI(infKey)+"/"+this.pageArray[this.pageIndex].methodName;
                this.pageArray[this.pageIndex].response = '';

                //发送请求到服务端
                let onIp = true;
                if(!this.pageArray[this.pageIndex].ip){
                    onIp = false;
                }

                let params = {};
                if(onIp == true){
                    params = {
                        "zk":ezk,
                        "dubboIp":eip,
                        "path":path,
                        "content":this.pageArray[this.pageIndex].request,
                    }
                }else{
                    params = {
                        "zk":ezk,
                        "dubboIp":'',
                        "path":path,
                        "content":this.pageArray[this.pageIndex].request,
                    }
                }

                this.pageArray[this.pageIndex].isSending = true;

                this.$NProgress.start();
                doRequest(params).then((res) => {
                    let status = res.status;
                    if(status != 200){
                        this.$message.error("服务器错误:"+status);
                    }

                    console.log("结果",res);
                    let ms = res.data;
                    this.pageArray[this.pageIndex].response = JSON.stringify(ms);

                    this.formatContent1();

                    this.pageArray[this.pageIndex].isSending = false;
                }).catch((err) => {
                    this.pageArray[this.pageIndex].isSending = false;
                    this.$message(
                        {
                            message: err.message,
                            type: 'error',
                            duration: 1 * 1000
                        }
                    );
                }).finally(() => {
                    this.$NProgress.done();
                });
            },
            getZkList(){
                let para = {};
                getAllZk(para).then((res) => {
                    let ms = res.data.data;
                    this.pageArray[this.pageIndex].zkList = ms;
                });
            },
            getAllService(){
                let ezk = encodeURI(this.pageArray[this.pageIndex].zk);
                let params = {"zk":ezk};
                getRegisterService(params).then((res) => {
                    this.pageArray[this.pageIndex].serviceNames = res.data.data;
                });
            },
            getProviders(){
                let ezk = encodeURI(this.pageArray[this.pageIndex].zk);
                let params = {"zk":ezk,"serviceName":this.pageArray[this.pageIndex].serviceName};
                return getAllProviders(params).then((res) =>{
                    let ms = res.data.data;
                    this.pageArray[this.pageIndex].providerNameMap = ms;
                    this.pageArray[this.pageIndex].providers = [];
                    for(let item in ms){
                        this.pageArray[this.pageIndex].providers.push(item);
                    }
                });
            },
            getMethods(){
                let ezk = encodeURI(this.pageArray[this.pageIndex].zk);
                console.log("providerKey请求:",this.pageArray[this.pageIndex].providerName);
                let infKey = encodeURI(this.pageArray[this.pageIndex].providerName);
                let params = {
                    "zk":ezk,
                    "serviceName":this.pageArray[this.pageIndex].serviceName,
                    "interfaceKey":infKey
                };
                console.log("请求:",params);
                getAllMethods(params).then((res) =>{
                    let ms = res.data.data.methods;
                    this.pageArray[this.pageIndex].ips =res.data.data.serverIps;
                    this.pageArray[this.pageIndex].methodNames = ms;
                });
            },
            getRequest(){
                let ezk = encodeURI(this.pageArray[this.pageIndex].zk);
                let providerName = this.pageArray[this.pageIndex].provider;
                let providerKey = this.pageArray[this.pageIndex].providerNameMap[providerName];
                let infKey = encodeURI(providerKey);
                let params = {
                    "zk":ezk,
                    "methodPath":this.pageArray[this.pageIndex].methodName,
                    "serviceName":this.pageArray[this.pageIndex].serviceName,
                    "interfaceKey":infKey
                };

                const loading = this.$loading({
                    lock: true,
                    text: '加载请求参数......',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });

                getTemplate(params).then((res) => {
                    let ms = res.data.data;
                    let code = res.data.code;
                    let error = res.data.error;

                    if(code != 0){
                        loading.close();
                        this.$message.error("请求异常:"+error);
                    }else{
                        this.pageArray[this.pageIndex].request = JSON.stringify(ms);
                        this.formatContent();
                        loading.close();
                    }
                });
            },
            saveNewTemplate(){
                console.log("保存:",this.pageArray[this.pageIndex].groupName,this.pageArray[this.pageIndex].caseName);
                if(!this.pageArray[this.pageIndex].groupName || !this.pageArray[this.pageIndex].caseName){
                    this.$message.error("未选择测试用例!");
                    return;
                }
                this.pageArray[this.pageIndex].dialogFormVisible = false;

                let providerName = this.pageArray[this.pageIndex].provider;
                let infKey = this.pageArray[this.pageIndex].providerNameMap[providerName];

                let caseDto = {
                    "groupName":this.pageArray[this.pageIndex].groupName,
                    "caseName":this.pageArray[this.pageIndex].caseName,
                    "zkAddress":this.pageArray[this.pageIndex].zk,
                    "serviceName":this.pageArray[this.pageIndex].serviceName,
                    "className":this.pageArray[this.pageIndex].provider,
                    "providerName":infKey,
                    "methodName":this.pageArray[this.pageIndex].methodName,
                    "requestValue":this.pageArray[this.pageIndex].request,
                    "responseValue":this.pageArray[this.pageIndex].response,
                };
                console.log("用例:",caseDto);
                saveCase(caseDto).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.queryGroupWithCase();
                        this.queryAllGroupOnlyNames();
                        //更新选择框
                        this.pageArray[this.pageIndex].groupNames=[this.pageArray[this.pageIndex].groupName,
                            this.pageArray[this.pageIndex].caseName];
                        this.pageArray[this.pageIndex].response = '';
                        this.$message.success("保存成功");
                    }else{
                        this.$message.error("保存失败");
                    }
                });
            },
            queryGroupWithCase:function () {
                getGroupAndCaseName({}).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.pageArray[this.pageIndex].groupWithCase = res.data.data;
                    }else{
                        this.$message.error("查询所有用例失败");
                    }
                });
            },
            queryAllGroupOnlyNames:function () {
                getAllGroupName({}).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.pageArray[this.pageIndex].groupOnlyNames = res.data.data;
                    }else{
                        this.$message.error("查询所有用例的组名失败");
                    }
                });
            },
            changeZk:function (value) {
                console.log("current zk:",value);
                if(!this.pageArray[this.pageIndex].autoTriggerWatch){
                    return;
                }
                this.pageArray[this.pageIndex].serviceNames=[];
                let serviceName = window.localStorage.getItem('serviceName');
                if(serviceName){
                    //存在local,不变,因为上一个页面传入
                }else{
                    //否则清空
                    this.pageArray[this.pageIndex].serviceName = '';
                }
                this.pageArray[this.pageIndex].providers=[];
                this.pageArray[this.pageIndex].provider="";

                this.pageArray[this.pageIndex].ips=[];
                this.pageArray[this.pageIndex].ip='';
                this.pageArray[this.pageIndex].onIp=false;

                this.pageArray[this.pageIndex].methodName = '';
                this.pageArray[this.pageIndex].methodNames = [];

                this.pageArray[this.pageIndex].request = '';
                this.pageArray[this.pageIndex].response = '';

                this.getAllService();
            },
            changeService:function (value) {
                console.log("current service:",value);
                if(!this.pageArray[this.pageIndex].autoTriggerWatch){
                    return;
                }
                this.pageArray[this.pageIndex].providers=[];
                this.pageArray[this.pageIndex].provider="";

                this.pageArray[this.pageIndex].ips=[];
                this.pageArray[this.pageIndex].ip='';
                this.pageArray[this.pageIndex].onIp=false;

                this.pageArray[this.pageIndex].methodName = '';
                this.pageArray[this.pageIndex].methodNames = [];

                this.pageArray[this.pageIndex].request = '';
                this.pageArray[this.pageIndex].response = '';

                this.getProviders();
            },
            changeProvider:function (value) {
                console.log("current provider:",value);
                let map = this.pageArray[this.pageIndex].providerNameMap;
                this.pageArray[this.pageIndex].providerName = map[value];

                if(!this.pageArray[this.pageIndex].autoTriggerWatch){
                    return;
                }
                this.pageArray[this.pageIndex].methodName = '';
                this.pageArray[this.pageIndex].methodNames = [];

                this.pageArray[this.pageIndex].request = '';
                this.pageArray[this.pageIndex].response = '';

                this.pageArray[this.pageIndex].ips=[];
                this.pageArray[this.pageIndex].ip='';

                this.getMethods();
            },
            changeMethodName:function (value) {
                console.log("current methodName:",value);
                if(!this.pageArray[this.pageIndex].autoTriggerWatch){
                    return;
                }

                this.pageArray[this.pageIndex].groupNames = [];
                //this.pageArray[this.pageIndex].groupName = '';
                this.pageArray[this.pageIndex].caseName = '';

                this.pageArray[this.pageIndex].request = '';
                this.pageArray[this.pageIndex].response = '';

                this.getRequest();
            },
            clearPageCache(){
                window.localStorage.clear();
                //window.localStorage.removeItem(this.cachePageName);
                window.location.reload();
            },
            // 复制成功
            onCopy(e){
                this.$message({
                    message:'复制成功！',
                    type:'success'
                })
            },
            // 复制失败
            onError(e){
                this.$message({
                    message:'复制失败！',
                    type:'error'
                })
            },
            async afterCreateService(pageItem){
                console.log("开始创建服务后的操作",pageItem);
                //这个操作不能在复制到其他元素之前执行!
                //设置创建服务传递进来的参数
                if(this.$route.query.zk){
                    pageItem.zk = this.$route.query.zk;
                    let ezk = encodeURI(pageItem.zk);
                    let params = {"zk":ezk};
                    let allService = await getRegisterService(params);
                    pageItem.serviceNames = allService.data.data;
                    //
                    pageItem.serviceName = this.$route.query.serviceName;

                    pageItem.providers=[];
                    pageItem.provider="";

                    pageItem.ips=[];
                    pageItem.ip='';

                    pageItem.methodName = '';
                    pageItem.methodNames = [];

                    pageItem.request = '';
                    pageItem.response = '';

                    pageItem.groupNames = [];

                    //查询关联的provider
                    params = {"zk":ezk,"serviceName":this.$route.query.serviceName};
                    let allProvider = await getAllProviders(params);

                    let ms = allProvider.data.data;
                    pageItem.providerNameMap = ms;
                    pageItem.providers = [];
                    for(let item in ms){
                        pageItem.providers.push(item);
                    }
                }
            }
        },
        async beforeMount(){
            console.log("加载之前",this.$route.query);
            let pageArrayStr = window.localStorage.getItem(this.cachePageName);
            this.pageArray = JSON.parse(pageArrayStr);
            if(!this.pageArray){
                console.log("初始化新数组");

                this.pageArray = [];
                //初始化所有的数组
                this.pageArray.push(this.pageItem);

                for(let i=0;i<this.tabMapOptions.length-1;i++){
                    let pageTmpItem = JSON.parse(JSON.stringify(this.pageItem));
                    this.pageArray.push(pageTmpItem);
                }

                //这个操作不能在复制到其他元素之前执行!
                //设置创建服务传递进来的参数
                this.afterCreateService(this.pageItem);

                let resZk = await getAllZk({});
                let resCase = await getGroupAndCaseName({});
                let resGroup = await getAllGroupName({});
                for(let i=0;i<this.tabMapOptions.length;i++){
                    let ms = resZk.data.data;
                    this.pageArray[i].zkList = ms;
                    console.log("查询zk列表",ms);

                    let code = resCase.data.code;
                    if(code == 0){
                        this.pageArray[i].groupWithCase = resCase.data.data;
                    }else{
                        this.$message.error("查询用例失败");
                    }

                    code = resGroup.data.code;
                    if(code == 0){
                        this.pageArray[i].groupOnlyNames = resGroup.data.data;
                    }else{
                        this.$message.error("查询用例所有组名失败");
                    }
                }

                window.localStorage.setItem(this.cachePageName,JSON.stringify(this.pageArray));
            }else{
                console.log("已有值重新加载");
                //加载缓存的值,设置创建服务的参数
                //这个操作不能在复制到其他元素之前执行!
                //设置创建服务传递进来的参数
                this.afterCreateService(this.pageArray[0]);
            }

            let res = await getGroupAndCaseName({});
            let code = res.data.code;
            if(code == 0){
                this.pageArray[this.pageIndex].groupWithCase = res.data.data;
            }else{
                this.$message.error("查询所有用例失败");
            }

            res = await getAllGroupName({});
            code = res.data.code;
            if(code == 0){
                this.pageArray[this.pageIndex].groupOnlyNames = res.data.data;
            }else{
                this.$message.error("查询所有用例的组名失败");
            }
            console.log("页面数组:",this.pageArray);
        },
        async mounted(){
        },
        destroyed: function () {
            window.localStorage.setItem(this.cachePageName,JSON.stringify(this.pageArray));
        },
        computed: {
            codemirror() {
                return this.$refs.myCm.codemirror
            },
            codemirror1(){
                return this.$refs.myCm1.codemirror
            }
        },
        watch:{

        }
    }
</script>

<style>
    .my-button{
        text-align:left;
        width: 100%;
        height: 100%;
        display: inline-table;
    }
    .CodeMirror {
        font-size:20px;
        border: 1px solid #eee;
        height: auto;
    }

    .CodeMirror-fullscreen {
        z-index: 9999!important;
    }

    .CodeMirror-scroll {
        max-height: 800px;
        height: auto;
        /*overflow-y: hidden;*/
        overflow-x: auto;
    }

    .el-select {
        width: 100%;
    }
    .el-cascader {
        width: 100%;
    }

    .cm-s-monokai.CodeMirror{
        height: 500px;
    }
</style>