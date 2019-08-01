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
    <div class="app-container">
        <el-form label-width="90px"
                 @submit.prevent="onSubmit"
                 style="margin:10px;width:98%;min-width:600px;">

            <el-tabs v-model="pageActiveName" type="border-card" @tab-click="tabSwitch">
                <el-tab-pane label="场景管理" key="0" name="场景管理">
                    <el-table
                            border
                            :row-class-name="tableRowClassName"
                            :data="getSceneFilterData()">
                        <el-table-column
                                align="left">

                            <el-table-column type="index" ></el-table-column>

                            <el-table-column label="场景名称">
                                <template slot-scope="scope">
                                    {{scope.row}}
                                </template>
                            </el-table-column>
                            <el-table-column label="操作">

                                <template slot="header" slot-scope="scope">
                                    <el-input
                                            v-model="scenesearch"
                                            size="mini"
                                            placeholder="输入名称搜索"/>
                                </template>

                                <template slot-scope="scope">

                                    <el-button
                                            type="primary"
                                            size="mini"
                                            plain
                                            @click="handleSelectAssociation(scope.$index, scope.row)">添加到场景</el-button>

                                    <el-button class="cpLink" size="mini" type="info" plain v-clipboard:error="onError"
                                               v-clipboard:copy="scope.row"  v-clipboard:success="onCopy">
                                        复制场景名称
                                    </el-button>

                                    <el-button
                                            type="warning"
                                            size="mini"
                                            @click="handleDeleteAssociation(scope.$index, scope.row)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            background
                            @current-change="handleSceneCurrentChange"
                            :page-size="scenepageSize"
                            :total="scenetotal"
                            style="float:right;">
                    </el-pagination>
                </el-tab-pane>
                <el-tab-pane label="用例管理" :key="1" name="用例管理">
                    <el-table stripe
                              border
                              :data="getFilterData()"
                    >

                        <el-table-column type="expand">
                            <template slot-scope="props">
                                <el-table :data="[props.row]">
                                    <el-table-column label="接口名称" prop="className" width="200">
                                    </el-table-column>
                                    <el-table-column label="方法名称" prop="methodName" width="300">
                                    </el-table-column>
                                    <el-table-column label="应用名称" prop="serviceName" width="200">
                                    </el-table-column>
                                    <el-table-column label="用例组名称" prop="groupName" width="100">
                                    </el-table-column>
                                    <el-table-column label="用例名称" prop="caseName" width="300">
                                    </el-table-column>
                                    <el-table-column label="接口路径" prop="providerName" width="300">
                                    </el-table-column>
                                    <el-table-column label="ZK" prop="zkAddress">
                                    </el-table-column>
                                </el-table>
                                <pre>{{props.row.requestValue}}</pre>
                                <pre>==================分隔线===================</pre>
                                <pre>{{props.row.responseValue}}</pre>
                            </template>
                        </el-table-column>

                        <el-table-column label="接口名称" prop="className" width="200">
                        </el-table-column>
                        <el-table-column label="方法名称" prop="methodName" width="300">
                        </el-table-column>
                        <el-table-column label="应用名称" prop="serviceName">
                        </el-table-column>

                        <el-table-column label="组名称" prop="groupName">
                        </el-table-column>

                        <el-table-column label="用例名称" prop="caseName">
                        </el-table-column>

                        <el-table-column
                                align="right" width="300px">
                            <template slot="header" slot-scope="scope">
                                <el-input
                                        v-model="search"
                                        size="mini"
                                        placeholder="输入服务|组|用例名称搜索"/>
                            </template>

                            <template slot-scope="scope">

                                <el-button
                                        type="primary"
                                        size="mini"
                                        plain
                                        :disabled = "scope.row.hasAdd"
                                        @click="handleSelect(scope.$index, scope.row)">添加到场景</el-button>

                                <el-button
                                        type="warning"
                                        size="mini"
                                        @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            background
                            @current-change="handleCurrentChange"
                            :page-size="pageSize"
                            :total="total"
                            style="float:right;">
                    </el-pagination>
                </el-tab-pane>
                <!--<el-tab-pane label="javaDoc" :key="2" name="javaDoc">
                    <iframe height="500px" width="100%" src="http://java-doc.com/">

                    </iframe>
                </el-tab-pane>-->
            </el-tabs>

            <br/>
            <el-table
                    border
                    ref="dragTable"
                    row-key="id"

                    highlight-current-row
                :data="selectGroupWithCases"
            >
                <el-table-column
                        label="ID"
                        prop="id">
                </el-table-column>
                <el-table-column label="接口名称" prop="className">
                </el-table-column>
                <el-table-column label="方法名称" prop="methodName" width="300px">
                </el-table-column>
                <el-table-column label="应用名称" prop="serviceName">
                </el-table-column>

                <el-table-column label="组名称" prop="groupName">
                </el-table-column>

                <el-table-column label="用例名称" prop="caseName">

                </el-table-column>

                <el-table-column
                        label="操作"
                        align="right"
                        width="500px">
                    <template slot-scope="scope">

                        <el-popover
                                placement="top"
                                width="800"
                                trigger="click">
                             <pre>{{scope.row.requestValue}}</pre>

                            <el-button size="mini" slot="reference">请求内容</el-button>
                        </el-popover>

                        <el-popover
                                placement="top"
                                width="800"
                                trigger="click">
                            <pre>{{scope.row.responseValue}}</pre>

                            <el-button size="mini" slot="reference">返回内容</el-button>
                        </el-popover>

                        <svg-icon class="drag-handler" icon-class="drag"/>

                        <el-button class="cpLink" size="mini" type="info" plain v-clipboard:error="onError"
                                   v-clipboard:copy="scope.row.caseName"  v-clipboard:success="onCopy">
                            复制用例名称
                        </el-button>

                        <el-button
                                type="warning"
                                size="mini"
                                @click="handleSelectRemove(scope.$index, scope.row)">移除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="codemirror">
                <el-alert
                        title="场景脚本："
                        type="info"
                        :closable="false">
                </el-alert>
                <codemirror
                            v-model="sceneScript"
                            :options="jscmOptions">
                </codemirror>
            </div>
            <br/>
            <el-button :loading="isRunning" type="success" v-on:click="runSelectCases">运行场景</el-button>

            <el-dialog title="保存到场景用例" :visible.sync="dialogFormVisible">
                <el-form-item label="用例名称：" label-width="100px">
                    <el-select
                            v-model="newAssociationCaseName"
                            filterable
                            allow-create
                            placeholder="请选择或输入名称">
                        <el-option
                                v-for="item in associationCaseNames"
                                :key="item"
                                :label="item"
                                :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>

                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveNewAssociation">确 定</el-button>
                </div>
            </el-dialog>

            <el-button type="primary" @click="dialogFormVisible = true">保存场景</el-button>

            <el-row>
                <el-col >
                    <el-alert
                            title="脚本测试结果："
                            type="info"
                            :closable="false">
                    </el-alert>

                    <div v-for="(value,key) in scriptResult">
                        <div v-if="booleanValue(value)">
                            <el-tag :key="key" type='success' v-if="value">
                                通过
                            </el-tag>
                            <el-tag :key="key" type="danger" v-else>
                                失败
                            </el-tag>
                            {{key}}
                        </div>
                        <div v-else>
                            <el-tag :key="key" type='warning'>
                                {{key}}：
                            </el-tag>
                            <span>{{value}}</span>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <br/>
        </el-form>
    </div>
</template>

<script>

    import{queryAllCaseDetail,deleteDetail} from '@/api/testCase';
    import{batchCaseRun} from '@/api/caseRun';
    import{saveAssociationCase,getAllAssociationName,
        getAssociationCase,deleteAssociationCase} from '@/api/associationCase';

    import Sortable from 'sortablejs'

    export default {
        name: 'sceneManage',
        data() {
            return {
                scriptNotes:'//全局变量1:reqs代表请求数组,reqs[0]代表第一个请求\n' +
                    '//全局变量2:rst代表结果变量map\n' +
                    '\n' +
                    '//全局方法1:获取第一个请求的notifyDraftDTO.cityDtoList属性:\n' +
                    '//var result = getProp(reqs[0],"notifyDraftDTO.cityDtoList")\n' +
                    '//if(result !== false){//表示获取成功\n' +
                    '//   rst.put("result",result);\n' +
                    '//}\n' +
                    '\n' +
                    '//全局方法2:设置请求的属性值notifyDraftDTO.categoryId 为123\n' +
                    '//var isOk = setProp(reqs[0],"notifyDraftDTO.categoryId",123);\n' +
                    '//if(isOk === true){//表示设置成功\n' +
                    '//\trst.put("设置成功",isOk);\n' +
                    '//}else{\n' +
                    '// 设置失败,这时不用进行后面的请求了,没有意义\n' +
                    '//}\n' +
                    '\n' +
                    '//全局方法3:发送请求,得到返回值对象\n' +
                    '//var responseObj = send(reqs[0]);//如果请求失败，返回的时错误描述内容\n' +
                    '//rst.put("responseObj",responseObj);',

                sceneScript:'',

                currentSceneName:'',

                cachePageName:'cacheManageContent',

                pageActiveName:'场景管理',

                dialogFormVisible:false,

                newAssociationCaseName:'',

                associationCaseNames:['A'],

                scriptResult:{},

                sortable: null,
                oldList: [],
                newList: [],

                total:10,
                pageSize:5,
                page:1,
                search: '',

                scenetotal:10,
                scenepageSize:5,
                scenepage:1,
                scenesearch: '',

                caseDetailFormVisible:false,

                groupWithCases:[],

                caseDetail:[],

                selectGroupWithCases:[],

                isRunning:false,

                jscmOptions: {
                    // codemirror options
                    mode: 'text/javascript',

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
            }
        },
        created() {
        },
        methods:{
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
            tabSwitch(tab, event){
                console.log("当前元素:",tab.index);

            },
            booleanValue(value){
                console.log("是否布尔:",value);
                if(typeof(value) == "boolean"){
                    return true;
                }
                return false;
            },
            async getList() {
                this.$nextTick(() => {
                    this.setSort()
                });
            },
            setSort() {
                const el = this.$refs.dragTable.$el.querySelectorAll('.el-table__body-wrapper > table > tbody')[0]
                this.sortable = Sortable.create(el, {
                    ghostClass: 'sortable-ghost', // Class name for the drop placeholder,
                    setData: function(dataTransfer) {
                        // to avoid Firefox bug
                        // Detail see : https://github.com/RubaXa/Sortable/issues/1012
                        dataTransfer.setData('Text', '')
                    },
                    onEnd: evt => {
                        const targetRow = this.selectGroupWithCases.splice(evt.oldIndex, 1)[0]
                        this.selectGroupWithCases.splice(evt.newIndex, 0, targetRow)
                    }
                })
            },
            getFilterData:function(){
                this.search = this.search.trim();
                let filterRules = this.groupWithCases.filter((item,index) => {
                    if(!this.search ){
                        return true;
                    }
                    if(item.serviceName.toString().toLowerCase().includes(this.search.toLowerCase())){
                        return true;
                    }
                    if(item.zkAddress.toLowerCase().includes(this.search.toLowerCase())){
                        return true;
                    }
                    if(item.groupName.toString().toLowerCase().includes(this.search.toLowerCase())){
                        return true;
                    }
                    if(item.caseName.toString().toLowerCase().includes(this.search.toLowerCase())){
                        return true;
                    }
                    if(item.className){
                        if(item.className.toString().toLowerCase().includes(this.search.toLowerCase())){
                            return true;
                        }
                        if(item.methodName.toString().toLowerCase().includes(this.search.toLowerCase())){
                            return true;
                        }
                    }
                });
                this.total = filterRules.length;
                let startIndex = (this.page -1) * this.pageSize;
                let endIndex = this.page * this.pageSize;
                //过滤后的结果少，页数大于1的情况
                if(startIndex >= this.total){
                    this.page = 1;
                    startIndex = 0;
                }
                if(endIndex > this.total){
                    endIndex = this.total;
                }
                let displayRules = filterRules.filter((item,index) => {
                    if(index >= startIndex && index < endIndex ){
                        return true;
                    }
                });
                return displayRules;
            },
            getSceneFilterData:function(){
                this.scenesearch = this.scenesearch.trim();
                let filterRules = this.associationCaseNames.filter((item,index) => {
                    if(!this.scenesearch ){
                        return true;
                    }
                    if(item.toString().toLowerCase().includes(this.scenesearch.toLowerCase())){
                        return true;
                    }
                });
                this.scenetotal = filterRules.length;
                let startIndex = (this.scenepage -1) * this.scenepageSize;
                let endIndex = this.scenepage * this.scenepageSize;
                //过滤后的结果少，页数大于1的情况
                if(startIndex >= this.scenetotal){
                    this.scenepage = 1;
                    startIndex = 0;
                }
                if(endIndex > this.scenetotal){
                    endIndex = this.scenetotal;
                }
                let displayRules = filterRules.filter((item,index) => {
                    if(index >= startIndex && index < endIndex ){
                        return true;
                    }
                });
                return displayRules;
            },
            handleCurrentChange(val) {
                this.page = val;
            },
            handleSceneCurrentChange(val) {
                this.scenepage = val;
            },
            queryGroupWithCase:function () {
                queryAllCaseDetail({}).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        console.log("所有用例:",res.data.data);
                        this.groupWithCases = res.data.data;
                    }else{
                        this.$message.error("查询所有用例失败");
                    }
                });
            },
            handleDelete: function(index, row){
                this.$confirm('确认删除选中用例吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    console.log("删除detail:",row);
                    let param = {
                        groupName:row.groupName,
                        caseName:row.caseName
                    };
                    deleteDetail(param).then((rsp) => {
                        let code = rsp.data.code;
                        let error = rsp.data.error;
                        if(code == 0){
                            this.queryGroupWithCase();
                            this.$message({
                                message: '删除成功',
                                type: 'success'
                            });
                        }else{
                            this.$message({
                                message: '删除失败:'+error,
                                type: 'error'
                            });
                        }
                    });
                });
            },
            handleCaseDetail:function (index,row) {
                this.caseDetailFormVisible=true;
                this.caseDetail = [];
                if(!row.testScript){
                    row.testScript = '无';
                }
                this.caseDetail.push(row);
            },
            handleSelect:function (index, row) {
                console.log("是否添加:",row);
                let has = false;
                let tmpStartIndex = this.selectGroupWithCases.length;
                if(tmpStartIndex > 0){
                    //取最后一个元素的值
                    tmpStartIndex = this.selectGroupWithCases[tmpStartIndex-1].id;
                    tmpStartIndex++;
                }
                for (let i = 0; i < this.selectGroupWithCases.length; i++) {
                    if (this.selectGroupWithCases[i].groupName == row.groupName &&
                        this.selectGroupWithCases[i].caseName == row.caseName){
                        //删除原来的,添加新的,在切换页面的时候需要这样
                        row.hasAdd = true;
                        //删除原来的
                        this.selectGroupWithCases.splice(i, 1);

                        row.id = tmpStartIndex;
                        //添加新的
                        this.selectGroupWithCases.push(row);
                        has = true;
                        break;
                    }
                }
                if(has == false){
                    row.id = tmpStartIndex;
                    row.hasAdd = true;
                    this.selectGroupWithCases.push(row);
                    this.getList();
                }
            },
            handleSelectRemove:function (index, row) {
                console.log("所有元素:",this.selectGroupWithCases,index);
                //groupWithCases
                for(let i = 0; i < this.groupWithCases.length; i++){
                    if (this.groupWithCases[i].groupName == row.groupName &&
                        this.groupWithCases[i].caseName == row.caseName){
                        //删除原来的,添加新的,在切换页面的时候需要这样
                        this.groupWithCases[i].hasAdd = false;

                        break;
                    }
                }
                this.selectGroupWithCases.splice(index, 1);
            },
            runSelectCases:function () {
                if(this.selectGroupWithCases.length<=0){
                    this.$message({
                        message: '至少选择一个用例',
                        type: 'error'
                    });
                    return;
                }
                console.log("所有元素:",this.selectGroupWithCases);

                let groupWithCaseArray = [];
                for (let i = 0; i < this.selectGroupWithCases.length; i++) {
                    let item = this.selectGroupWithCases[i];
                    let simpleItem = {
                        groupName:item.groupName,
                        caseName:item.caseName,
                    }
                    groupWithCaseArray.push(simpleItem);
                }

                let param = {
                    caseName:'',//运行的时候传空
                    sceneScript:this.sceneScript,
                    caseDtoList:groupWithCaseArray
                };
                console.log("场景脚本:",this.sceneScript);
                this.isRunning = true;
                //请求
                batchCaseRun(param).then((rsp) => {
                    let code = rsp.data.code;
                    let error = rsp.data.error;
                    if(code == 0){
                        this.$message({
                            message: '执行成功',
                            type: 'success'
                        });
                        this.scriptResult = rsp.data.data;
                        console.log("批量case执行结果:",rsp.data.data);

                    }else{
                        this.$message({
                            message: '执行失败:'+error,
                            type: 'error'
                        });
                    }

                    this.isRunning = false;
                });
            },
            formatContent(){
                var totalLines = this.codemirror.lineCount();
                this.codemirror.autoFormatRange(
                    {line:0, ch:0},
                    {line:totalLines},this.responseValue);
            },
            getAllAssociationCaseName(){
                return getAllAssociationName().then((rsp) => {
                    let code = rsp.data.code;
                    let error = rsp.data.error;
                    if(code == 0){
                        this.associationCaseNames = rsp.data.data;
                    }else{
                        this.$message({
                            message: '查询失败:'+error,
                            type: 'error'
                        });
                    }
                });
            },
            handleSelectAssociation(indexy, row){
                console.log("查询场景:",row);
                let params = {'caseName':row};
                getAssociationCase(params).then((rsp) => {
                    let code = rsp.data.code;
                    let error = rsp.data.error;
                    if(code == 0){

                        for(let i = 0; i < this.selectGroupWithCases.length; i++){
                            //对所有元素做一遍remove,保证用例管理列表的显示状态是正常的,不然不能点击添加
                            let item = this.selectGroupWithCases[i];
                            for(let j = 0; j < this.groupWithCases.length; j++){
                                if (this.groupWithCases[j].groupName == item.groupName &&
                                    this.groupWithCases[j].caseName == item.caseName){
                                    this.groupWithCases[j].hasAdd = false;
                                    break;
                                }
                            }
                        }

                        let rspData = rsp.data.data;
                        this.selectGroupWithCases = [];
                        for(let index in rspData.caseDtoList){
                            rspData.caseDtoList[index].id = index;
                        }
                        //caseDtoList
                        this.selectGroupWithCases = rspData.caseDtoList;
                        this.sceneScript = '';
                        if(rspData.sceneScript){
                            this.sceneScript = rspData.sceneScript;
                        }else{
                            this.sceneScript = this.scriptNotes;
                        }

                        this.scriptResult = '';
                        this.currentSceneName = row;
                        this.newAssociationCaseName = row;

                        this.getList();

                    }else{
                        this.$message({
                            message: '查询失败:'+error,
                            type: 'error'
                        });
                    }
                });
            },
            handleDeleteAssociation(index, row){
                console.log("删除元素：",row);
                this.$confirm('确认删除选中场景吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    deleteAssociationCase({caseName:row}).then((rsp) => {
                        let code = rsp.data.code;
                        let error = rsp.data.error;
                        if(code == 0){
                            this.getAllAssociationCaseName();
                            this.$message({
                                message: '删除成功',
                                type: 'success'
                            });
                        }else{
                            this.$message({
                                message: '删除失败:'+error,
                                type: 'error'
                            });
                        }
                    });
                });
            },
            async saveNewAssociation(){
                let caseName = this.newAssociationCaseName;
                let param = {
                    caseName:caseName,
                    caseDtoList:this.selectGroupWithCases,
                    sceneScript:this.sceneScript
                }
                let saveRsp = await saveAssociationCase(param);
                {
                    let code = saveRsp.data.code;
                    let error = saveRsp.data.error;
                    if(code == 0){
                        let getRsp = await getAllAssociationName();
                        {
                            let code = getRsp.data.code;
                            let error = getRsp.data.error;
                            if(code == 0){
                                this.associationCaseNames = getRsp.data.data;
                                this.currentSceneName = caseName;
                                this.handleSelectAssociation(0,caseName);
                                this.$message({
                                    message: '保存成功',
                                    type: 'success'
                                });
                            }else{
                                this.$message({
                                    message: '查询场景失败:'+error,
                                    type: 'error'
                                });
                            }
                        }
                    }else{
                        this.$message({
                            message: '保存失败:'+error,
                            type: 'error'
                        });
                    }
                }

                this.dialogFormVisible = false;
            },
            tableRowClassName({row, rowIndex}){
                console.log("场景class:",row,this.currentSceneName);
                if (row === this.currentSceneName) {
                    return 'success-row'
                }
                return '';
            },
            async syncInit(){
                const rsp = await queryAllCaseDetail({});
                {
                    let code = rsp.data.code;
                    if(code == 0){
                        console.log("所有用例:",rsp.data.data);
                        this.groupWithCases = rsp.data.data;
                    }else{
                        this.$message.error("查询所有用例失败");
                    }
                }

                let tmpSceneScript = window.localStorage.getItem(this.cachePageName+"_script");
                if(tmpSceneScript){
                    this.sceneScript = tmpSceneScript;
                }
                let arrayStr = window.localStorage.getItem(this.cachePageName);
                this.selectGroupWithCases = JSON.parse(arrayStr);

                if(!this.selectGroupWithCases){
                    this.selectGroupWithCases = [];
                    //nothing to do
                }else{
                    let arrayStr = window.localStorage.getItem(this.cachePageName);
                    this.selectGroupWithCases = JSON.parse(arrayStr);
                    this.getList();

                    let tmpArray = [];
                    for(let i = 0; i < this.selectGroupWithCases.length; i++){
                        //对所有元素做一遍替换
                        let oldItem = this.selectGroupWithCases[i];
                        for(let j = 0; j < this.groupWithCases.length; j++){
                            let newItem = this.groupWithCases[j];
                            if (newItem.groupName == oldItem.groupName &&
                                newItem.caseName == oldItem.caseName){
                                newItem.hasAdd = true;
                                newItem.id = oldItem.id;
                                //更新当前新的元素
                                tmpArray.push(this.groupWithCases[j]);
                            }
                        }
                    }
                    this.selectGroupWithCases = tmpArray;
                }

                this.currentSceneName = window.localStorage.getItem(this.cachePageName+"_1");

                let scriptResultStr = window.localStorage.getItem(this.cachePageName+"_2");
                this.scriptResult = JSON.parse(scriptResultStr);
                if(!this.scriptResult){
                    this.scriptResult = {};
                }
            }
        },
        mounted(){
            this.syncInit();
            this.getAllAssociationCaseName();
        },
        destroyed: function () {

            let arrayStr = JSON.stringify(this.selectGroupWithCases);
            window.localStorage.setItem(this.cachePageName,arrayStr);

            window.localStorage.setItem(this.cachePageName+"_1",this.currentSceneName);

            let scriptResultStr = JSON.stringify(this.scriptResult);

            window.localStorage.setItem(this.cachePageName+"_2",scriptResultStr);
            //脚本
            window.localStorage.setItem(this.cachePageName+"_script",this.sceneScript);
        },
        computed: {
            codemirror() {
                return this.$refs.myCm.codemirror
            },
        },
    }
</script>
<style scoped>

</style>
<style>
    .el-table .success-row {
        background: #f0f9eb;
    }

    .drag-handler{
        width: 20px;
        height: 20px;
        cursor: pointer;
    }

    pre {
        white-space: pre-wrap;
        word-wrap: break-word;
        font-size:16px;
        max-height:600px;
        overflow:auto;
        font-family:monospace;
    }
</style>