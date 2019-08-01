DUBBO-POSTMAN
==========

<img src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/logo.png?raw=true" alt="dubbo postman logo" height="90px" align="right" />

**DUBBO-POSTMAN**: 一个用于通过web-ui页面访问dubbo接口的工具,灵感源于
[postman](https://www.getpostman.com/products)

# 介绍
**DUBBO-POSTMAN** 是一个通过web页面访问dubbo接口的开源工具,包括零代码创建一个dubbo consumer,保存访问用例,构建场景测试.
核心功能如下.

- 通过添加一个api的maven dependency即可创建一个dubbo consumer
- webui页面自动生成dto的各个参数
- 保存常用的请求作为用例
- 根据保存的用例即可构建复杂的场景测试,极大减少回归测试的工作量
- 开箱即用

DUBBO-POSTMAN UI
===============

## 开发

 基于[vue](https://github.com/vuejs/vue)+
 [element-ui](https://element.eleme.cn/#/zh-CN)+
 [vue-element-admin](https://panjiachen.github.io/vue-element-admin-site/zh/).

### 准备

下载源码到本地

```
git clone https://github.com/everythingbest/dubbo-postman.git
npm install --registry=https://registry.npm.taobao.org
```

### 本地调试
```
npm run dev
```

### 发布
```
npm run build
```

默认的ui地址是 `http://localhost:9528/`.
后台服务地址是 `http://localhost:8080/`.

DUBBO-POSTMAN BACKEND
===============

首先需要确保代码已经下载到本地,后台的所有配置在项目的src/resource/application.properties文件里面

## 启动
在项目根目录下执行 maven clean package ,然后在命令行执行 java -jar target/dubbo-postman.jar


## application.properties
项目的所有核心配置都在这个文件里面.

在 `application.properties`有三个主要配置.
1. **dubbo.api.jar.dir**: maven-embedder构建的目录.
1. **nexus.url**: 私服仓库地址.
1. **nodex.ip**: redis哨兵配置.

使用演示
===============

<p align="center">
dubbo demo创建
<img align="center" alt="face" src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/dubbo-postman-1.gif?raw=true" />
</p>
<p align="center">
创建服务
 <img align="center" alt="face" src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/dubbo-postman-2.gif?raw=true" />
</p>
<p align="center">
场景测试
 <img align="center" alt="face" src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/dubbo-postman-3.gif?raw=true" />
</p>
<p align="center">
注册中心添加
 <img align="center" alt="face" src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/dubbo-postman-4.gif?raw=true" />
</p>

赞赏
===============

如果你觉得这个项目帮助到了你，你可以帮作者买一杯果汁表示鼓励 🍹
<table>
  <tr>
      <td width="50%" align="center"><b>AliPay</b></td>
      <td width="50%" align="center"><b>WechatPay</b></td>
  </tr>
  <tr>
     <td><img src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/alipay.JPG?raw=true"/></td>
     <td><img src="https://github.com/everythingbest/dubbo-postman/blob/master/gif/wechatpay.JPG?raw=true"/></td>
  </tr>
</table>