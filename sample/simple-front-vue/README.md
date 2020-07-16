

## 简介

这是一个演示项目，使用前端Vue技术栈（vue 2.0 + vue.router + vuex），对接PAuth实现oauth 2.0 authorization code grant授权方式，实现用户登录功能。

## 项目的结构和构建

```
- README.md 使用说明文档
- package.json NPM构建配置文件
- webpack.config.js webpack打包配置文件
+ src
  - index.html 前端应用主页面
  - main.js 前端应用入口
  + api 对后端API接口的调用
  + assets 静态资源文件
  + components VUE组件
  + pages 应用分页面
  + router 前端应用路由
  + store 前端应用数据模型层
```

#### 安装node并配置npm源

登录node官方网站安装node 3.10.8+。

配置npm源为淘宝源

npm set registry "https://registry.npm.taobao.org/"

配置后可以通过npm config list查看。

#### 前端项目构建运行命令

构建文件：./package.json

下载依赖包：npm install，执行成功后将会把所有的项目依赖包下载到/node_modules目录。

构建命令：npm run build，构建成功后将会生成./dist目录。

运行命令：npm run dev，运行成功后可以通过[http://localhost:8400](http://localhost:8400)访问应用

注：应用的启动端口在./webpack.config.js文件中devServer.port选项所配置。

## 演示步骤

1. 准备工作
   - 根据auth-server项目的README文档，启动pauth server。
   - 根据auth-front项目的README文档，启动pauth前端。
   - 若以项目缺省配置，pauth server和前端分别启动在如下两个地址，
   ```
   pauth后端服务 http://localhost:8090
   pauth前端服务 http://localhost
   ```
   - 本演示项目将根据上面的两个地址进行授权登录跳转。
   - 注册应用，请登录pauth前端页面，并注册应用client id = demo，重定向返回地址为：.* （其含义为pauth server接受任何client指定的返回地址）

2. 启动当前演示项目
   - 执行前端应用运行命令：npm run dev
   - 打开浏览器，访问前端应用地址：http://localhost:8400
   - 点击右上角hello登录按钮，将跳转到pauth server的授权界面：http://localhost/#/authorize，点击同意按钮。注：若pauth server没有登录，则需要先登录，再点击同意授权按钮。
   - 若一切正常，pauth server将跳转回当前前端演示应用地址，并完成登录，显示登录账号
