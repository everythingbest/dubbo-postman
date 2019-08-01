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
        <el-form label-width="200px"
                 @submit.prevent="onSubmit"
                 style="margin:20px;width:90%;min-width:600px;">
            <el-row>
                <el-col :span="14">
                    <el-form-item label="注册中心：">
                        <el-select v-model="zk" placeholder="请选择注册ZK" filterable>
                            <el-option v-for="option in zkList" v-bind:value="option" :label="option">
                                {{ option }}
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row>
                <el-col :span="13">
                    <el-form-item label="服务名称：">
                        <el-select v-model="zkServiceName" placeholder="DUBBO服务名称" filterable>
                            <el-option v-for="option in serviceList" v-bind:value="option" :label="option">
                                {{ option }}
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>

                <el-col :span="1">
                    <el-form-item label-width="0px">
                        <el-button class="cpLink my-button" plain type="info" v-clipboard:error="onError"
                                   v-clipboard:copy="zkServiceName"  v-clipboard:success="onCopy">
                            复制
                        </el-button>
                    </el-form-item>
                </el-col>

            </el-row>

            <el-row>
                <el-col :span="14">

                    <el-form-item label="API MAVEN依赖：">
                    <el-input
                        type="textarea"
                        :autosize="{ minRows: 7, maxRows: 7}"
                        placeholder="推荐直接从nexus复制过来比较准确
<dependency>
  <groupId>com.xx.yy</groupId>
  <artifactId>cc-service-api</artifactId>
  <version>1.1.3-SNAPSHOT</version>
</dependency>"
                        v-model="dependency">
                    </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item>
                <a :href="'http://192.168.1.177:8081/nexus/#welcome'" target="_blank" class="el-button el-button--info">
                    NEXUS地址
                </a>
                <el-button :loading="isCreating" type="success" v-on:click="doCreate">创建</el-button>
            </el-form-item>

        </el-form>

        <el-dialog
                title="服务创建失败"
                :visible.sync="dialogVisible"
                :fullscreen="true"
                :show-close="false">
            <pre>{{rspMsg}}</pre>
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </el-dialog>
    </section>
</template>

<script>

    import { getZkServices,upload} from '@/api/create';
    import { getAllZk} from '@/api/common';

    export default {
        name:'createService',
        data() {
            return {
                dependency:'',
                isCreating:false,
                zk:'',
                zkServiceName:'',
                zkList: [],
                serviceList: [],
                rspMsg:'',
                dialogVisible:false
            }
        },
        methods: {
            onSubmit(){
                this.$message.success("创建成功onSubmit");
            },
            doCreate(){
                this.$confirm('确认创建服务吗？', '提示', {}).then(() => {
                    this.createService()
                });
            },
            async createService() {
                if(this.zk == '' ||
                    this.zkServiceName == '' ||
                    this.dependency == '' ){
                    this.$message.error('必须指定:zk,serviceName,dependency');
                    return;
                }
                var encodedZk = encodeURI(this.zk);

                let params = {"zk":encodedZk,
                    "zkServiceName":this.zkServiceName,
                    "dependency":this.dependency}

                this.isCreating = true;
                this.$NProgress.start();
                let res = await upload(params);
                this.$NProgress.done();
                this.isCreating = false;
                if (res.status == 200) {
                    if(res.data.code == 0){
                        this.$notify({
                            title: '创建服务',
                            message: this.zkServiceName+"创建成功!",
                            type: 'success'
                        });
                        //清空页面缓存
                        window.localStorage.clear();
                       //相当于页面刷新,注意name属性一定时组件的名称
                        this.$store.dispatch('tagsView/delCachedView', {name:'accessService'}).then(() => {
                            this.$nextTick(() => {
                                this.$router.push({
                                    path:'/redirect' +'/access/index',
                                    query:{
                                        zk:this.zk,
                                        serviceName:this.zkServiceName
                                    }
                                });
                            })
                        })
                    }else{
                        this.rspMsg = res.data.error;
                        this.dialogVisible = true;
                    }
                }else{
                    this.$notify({
                        title: '创建服务',
                        message: '系统错误,请重试或联系管理员,状态码:'+res.status,
                        type: 'error',
                        duration: 0
                    });
                }
            },
            getZkList(){
                let para = {};
                getAllZk(para).then((res) => {
                    let ms = res.data.data;
                    this.zkList = ms;
                });
            },
            getSelectedServices (){
                let zk = this.zk
                let params = {"zk":zk};
                getZkServices(params).then((res) => {
                    let data = res.data.data;
                    this.serviceList = data;
                });
            },
            onSubmit() {
                console.log('submit!');
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
        },
        watch:{
            zk: function() {
                this.zkServiceName = '';
                this.serviceList = [];
                this.getSelectedServices();
            }
        },
        mounted() {
            this.getZkList();
        }
    }
</script>
