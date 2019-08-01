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
  <el-menu class="navbar" mode="horizontal">
    <hamburger :toggle-click="toggleSideBar" :is-active="sidebar.opened" class="hamburger-container"/>
    <breadcrumb class="breadcrumb-container"/>

    <div class="right-menu">
      <div
              class="self-menu"
              @click="clearPageCache">
        <a>清空本地存储</a>
      </div>

      <el-dropdown trigger="hover" class="right-menu-item hover-effect" @command="handleSelectEnvironment">
        <div class="self-menu">
          <svg-icon icon-class="tree" />
          <a>环境切换-{{sysEnv}}</a>
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item class="self-menu-item"  command="http://dubbo-postman-dev.com">开发环境</el-dropdown-item>
          <el-dropdown-item class="self-menu-item" command="http://dubbo-postman-qa1.com">QA1环境</el-dropdown-item>
          <el-dropdown-item class="self-menu-item" command="http://dubbo-postman-qa3.com">QA3环境</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img src="@/assets/logo.png" class="user-avatar">
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link class="inlineBlock" to="/access/index">
            <el-dropdown-item>
              首页
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item divided>
            <span style="display:block;" @click="logout">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </el-menu>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'

import { getEnv} from '@/api/common';

export default {
    data() {
        return {
            sysEnv:'DEV',
        }
    },
  components: {
    Breadcrumb,
    Hamburger,
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  methods: {
      clearPageCache(){
          window.localStorage.clear();
          window.location.reload();
      },
      getSysEnv(){
          let param = {};
          getEnv(param).then((res) => {

              this.sysEnv = res.data.data;
          });
      },
      logout() {
          /*this.$store.dispatch('LogOut').then(() => {
            location.reload() // 为了重新实例化vue-router对象 避免bug
          });*/
          window.location = "/logout";
      },
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    },
    handleSelectEnvironment(lang) {
        console.log("连接:",lang);
        window.open(lang, '_blank', );
    },
    logout() {
      window.location = "/logout";
    }
  },
    mounted() {
        this.getSysEnv();
    }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .navbar {
    @import "src/main/frontend/styles/variables.scss";
    height: 50px;
    line-height: 50px;
    border-radius: 0px !important;
    .hamburger-container {
      line-height: 58px;
      height: 50px;
      float: left;
      padding: 0 10px;
    }
    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
    .right-menu {
      float: right;
      height: 100%;
      line-height: 50px;

      &:focus {
        outline: none;
      }

      .right-menu-item {
        display: inline-block;
        padding: 0 8px;
        height: 100%;
        font-size: 18px;
        color: #5a5e66;
        vertical-align: text-bottom;

        &.hover-effect {
          cursor: pointer;
          transition: background .3s;

          &:hover {
            background: rgba(0, 0, 0, .025)
          }
        }
      }

      .avatar-container {
        margin-right: 30px;

        .avatar-wrapper {
          margin-top: 5px;
          position: relative;

          .user-avatar {
            cursor: pointer;
            width: 40px;
            height: 40px;
            border-radius: 10px;
          }

          .el-icon-caret-bottom {
            cursor: pointer;
            position: absolute;
            right: -20px;
            top: 25px;
            font-size: 12px;
          }
        }
      }
    }
  }

  .self-menu{
    @import "src/main/frontend/styles/variables.scss";
    /*min-width: 180px !important;*/
    float: left;
    display: inline-block;
    padding: 0 8px;
    height: 100%;
    color: #495060;
    background: #fff;
    vertical-align: middle;

    &:hover {
      background-color: #42b983;
      color: #fff;
    }
    font-size: 18px;
  }

  .self-menu-item{
    @import "src/main/frontend/styles/variables.scss";
    /*min-width: 180px !important;*/
    padding: 0 8px;
    height: 100%;
    color: #495060;
    background: #fff;
    vertical-align: middle;

    &:hover {
      background-color: #42b983;
      color: #fff;
    }
    font-size: 16px;
  }
</style>
