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
    <div style="margin:20px;width:80%;min-width:600px;">
        <el-row >
            <el-col :offset="2">
                <el-table
                        :data="zkAddress"
                        style="width: 100%">
                    <el-table-column
                            label="IP"
                            width="480">
                        <template slot-scope="scope">
                            <span style="margin-left: 10px">{{ scope.row }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                    size="mini"
                                    type="danger"
                                    @click="deleteZk(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-col>
        </el-row>
        <el-form label-width="100px" style="margin:20px;width:95%;min-width:600px;">
            <el-row>
                <el-col>
                    <el-form-item label="ZK:">
                        <el-input v-model="zk"></el-input>
                    </el-form-item>
                </el-col>
                <el-col>
                    <el-form-item label="密码:">
                        <el-input v-model="password"></el-input>
                    </el-form-item>
                </el-col>
                <el-col>
                    <el-form-item>
                        <el-button type="primary" @click="onSubmit">新 增</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
    </div>
    </section>
</template>

<script>

    import { addConfig,deleteZk,configs} from '@/api/config';

    export default {
        name:'systemConfig',
        data(){
            return{
                requestCodeMirror:'',
                zk:'',
                password:'',
                zkAddress: [],
            }
        },
        methods:{
            onSubmit(){
                let encodedZk = encodeURI(this.zk);
                let params = {
                    "zk":encodedZk,
                    "password":this.password
                };
                const loading = this.$loading({
                    lock: true,
                    text: '正在初始化ZK配置,请耐心等待......',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                addConfig(params).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.$message.success("保存成功");
                        window.localStorage.clear();
                        window.location.reload();
                    }else{
                        let error = res.data.error
                        this.$message.error(error);
                    }
                    this.getConfigs();

                    setTimeout(() => {
                        loading.close();
                    }, 500);
                });
            },
            getConfigs(){
                configs().then((res) => {
                    let zkAddress = res.data.data;
                    console.log("返回的zk地址:",zkAddress);
                    this.zkAddress = zkAddress;
                });
            },
            deleteZk(index,value){
                let encodedZk = encodeURI(value);
                let params = {
                    "zk":encodedZk,
                    "password":this.password
                };
                deleteZk(params).then((res) => {
                    let code = res.data.code;
                    if(code == 0){
                        this.$message.success("删除成功");
                        window.localStorage.clear();
                        window.location.reload();
                    }else{
                        let error = res.data.error
                        this.$message.error(error);
                    }
                    this.getConfigs();
                });
            }
        },
        mounted(){
            this.getConfigs();
        }
    }
</script>