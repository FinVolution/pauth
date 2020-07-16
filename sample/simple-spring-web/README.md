## 简介
这是一个演示项目，前后端分离设计，使用spring web搭建后端服务，使用vue前端技术栈开发前端应用，对接PAuth实现oauth 2.0 authorization code grant授权方式，实现用户登录功能。整个登录过程中有web的重定向，应用程序不会接触到用户名和密码，这种授权方式适用于一般的web应用和第三方应用。

## 项目结构

```
+ front/src 前端
  + api
    + service
      - authService.js 定义了获取、刷新、吊销token以及获取登录地址四个请求
    - restApi.js 定义了GET、POST、INPUT、DELETE四种请求类型
  + components
    - Header.vue 实现了登录、过期检查、刷新token、吊销token等功能
  + pages
    - Login.vue 实现了接收授权码、获取token、登录成功跳转等功能
  + store/model
    - pauth.js 定义了登录、获取token、刷新token、吊销token、解析token、过期检查等方法
  + utils
    - jwtTokenUtil.js 封装了存储、读取和清除token的方法
    - lastVisitUtil.js 封装了存储、读取和清除用户最近访问地址的方法
  - main.js 添加了http request拦截器
+ server 后端
  + src/main/java
    + com.ppdai.auth.sample
      - SampleApp.java 主程序
      + controller 测试用接口
  + src/main/resources
    - application.properties 项目配置，包括授权服务器地址、过滤器类型、特殊url、client id/secret等
  - pom.xml Maven项目构建文档
- README.md 使用说明文档
```

## 演示步骤

### 1. 启动pauth server
- 请根据auth-server项目的README文档，启动pauth server
- 若根据项目的缺省配置，pauth server将启动在如下地址：

```
   http://localhost:8090
```

### 2. 启动pauth front
- 请根据auth-front项目的README文档，启动pauth前端管理页面
- 若根据项目的缺省配置，pauth front将启动在如下地址：

```
http://localhost
```

### 3. 注册client

- 打开pauth前端管理页面，输入用户名和密码登录。用户名和密码的账号信息请参考auth-front项目的README文档
- 登录成功后，点击应用开发->注册client，输入client名以及重定向返回地址（支持正则表达式，这里的作用主要是校验client后端配置的redirectUri，如不需要校验可填写.*），点击注册按钮
- 点击应用开发->我的client，可以查看注册的client信息，注意Basic Auth一栏，这是根据clientId:clientSecret生成的Base64加密字符串，接下来的后端配置中会用到

### 4. 添加依赖

在server目录下的pom.xml文件中添加如下依赖：

```xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-support</artifactId>
    <version>1.1.1</version>
</dependency>
```

这个依赖将给当前web应用引入pauth-support，关于pauth-support的更多使用方法请参见如下项目的README文件：
- auth-client/auth-spring-support/README.md

### 5. 项目配置

打开项目配置文件 server/src/main/resources/application.properties，添加如下配置：

```
# pauth spring client settings
pauth.api.url = http://localhost

# pauth spring filter settings
pauth.spring.filter.type = all-check-by-skip
pauth.spring.filter.token.store.type = header
pauth.spring.filter.token.name = jwt-token
pauth.spring.filter.special.urls = GET&.*

# pauth spring support settings
pauth.api.clientId = spring-demo
pauth.api.redirectUri = http://localhost:8888/#/login
pauth.api.authorization = Basic c3ByaW5nLWRlbW86QTlwcjE5
```

- clientId是在pauth系统上注册的client名
- redirectUri是client用来接收授权码的地址，与注册client时填写的重定向返回地址匹配才能进行授权
- authorization是注册client时生成的Basic Auth字符串，在获取、刷新和吊销token时起校验作用

### 6. 构建启动应用后端

- 进入server目录
- 构建命令：mvn compile package
- 启动命令：java -jar ./target/simple-spring-web-1.1.0.jar

根据项目缺省配置，应用将启动在如下地址：

```
http://localhost:8080
```

### 7. 构建启动应用前端

- 进入front目录
- 构建文件：./package.json
- 下载依赖包：npm install，执行成功后将会把所有的项目依赖包下载到./node_modules目录
- 构建命令：npm run build，构建成功后将会生成./dist目录
- 运行命令：npm run dev

根据项目缺省配置，应用将启动在如下地址：

```
http://localhost:8888
```

注：应用的启动端口在./webpack.config.js文件中的devServer.port选项配置。

### 8. 演示步骤

- 打开浏览器，访问应用前端地址：http://localhost:8888
- 点击右上角的登录按钮，将跳转到pauth的授权页面：http://localhost/#/authorize
- 点击同意按钮，若pauth没有登录，则需要先登录，再点击同意按钮
- 若一切正常，pauth将跳转回应用前端地址，并完成登录，显示登录账号
