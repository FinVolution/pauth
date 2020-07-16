## 简介
这个演示项目是simple-spring-web项目的升级版本，后端实现方式不变，前端则删除了大量代码，改为引入pauth-vue-support依赖，使得整个项目结构变得轻量简洁，只需在适当的地方引入依赖包中的组件和模块就可以实现授权登录功能。
## 项目结构

```
+ front 前端
  + src
    + assets
      - common.css 指定了用户头像和登录失败的背景图
    + pages
      - Layout.vue 引入了依赖包中的Header组件，指定了title
    + router
      - index.js 引入了依赖包中的Login组件
    + store
      - index.js 引入了依赖包中的pauth模块
    - main.js 引入了依赖包中的axios拦截器
  - package.json 引入了pauth-vue-support依赖
+ server 后端
  + src/main/java
    + com.ppdai.auth.sample
      + controller 测试用接口
      - SampleApp.java 主程序
  + src/main/resources
    - application.properties 项目配置，包括授权服务器地址、过滤器类型、特殊url、client id/secret等
  - pom.xml Maven项目构建文档
- README.md 使用说明文档
```

## 演示步骤

1. 启动pauth server
- 请根据auth-server项目的README文档，启动pauth server
- 若根据项目的缺省配置，pauth server将启动在如下地址：

```
   http://localhost:8090
```

2. 启动pauth front
- 请根据auth-front项目的README文档，启动pauth前端管理页面
- 若根据项目的缺省配置，pauth front将启动在如下地址：
```
http://localhost
```
3. 注册client

- 打开pauth前端管理页面，输入用户名和密码登录。用户名和密码的账号信息请参考auth-front项目的README文档
- 登录成功后，点击应用开发->注册client，输入client名以及重定向返回地址（支持正则表达式，这里的作用主要是校验client后端配置的redirectUri，如不需要校验可填写.*），点击注册按钮
- 点击应用开发->我的client，可以查看注册的client信息，注意Basic Auth一栏，这是根据clientId:clientSecret生成的Base64加密字符串，接下来的后端配置中会用到

4. 添加依赖

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

5. 项目配置

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

6. 构建启动应用后端

- 进入server目录
- 构建命令：mvn compile package
- 启动命令：java -jar ./target/simple-spring-web-1.1.0.jar

根据项目缺省配置，应用将启动在如下地址：

```
http://localhost:8080
```

7. 构建启动应用前端

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

8. 演示步骤

- 打开浏览器，访问应用前端地址：http://localhost:8888
- 点击右上角的登录按钮，将跳转到pauth的授权页面：http://localhost/#/authorize
- 点击同意按钮，若pauth没有登录，则需要先登录，再点击同意按钮
- 若一切正常，pauth将跳转回应用前端地址，并完成登录，显示登录账号
