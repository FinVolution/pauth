README
===========================

## 准备
### 下载项目并切换到开发分支
git clone pauth



## 项目构建

本项目为前端node项目，技术栈为vue 2.0 + vue.router + vuex + bootstrap-vue/element + axios + element ui + echarts

请参考如下步骤对项目进行构建启动，


### 0. 安装node并配置npm源

登录node官方网站安装node 3.10.8+。

配置npm源为淘宝源

npm set registry "https://registry.npm.taobao.org/"

配置后可以通过npm config list查看。


### 1. 前端项目构建

构建文件：./package.json

构建命令：npm install

### 2. 配置域名

配置hosts文件，
- windows中打开文件夹 C:\Windows\System32\drivers\etc\hosts
- Linux中打开文件夹 /ets/hosts

添加如下域名映射，
    
其中YOUR_LOCAL_IP为你本地机器IP地址。

### 3. 启动后端服务

后端项目的启动见admin-server的README文件，启动后会默认运行在端口8090。

### 4. 开发调试运行

命令：npm run dev

启动配置在./webpack.config.js文件中的devServer.host和devServer.port选项，启动后访问http://host:port。

### 5. 前端访问并登录

