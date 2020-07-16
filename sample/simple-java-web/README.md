这是一个演示项目，使用Java web后端模板技术栈（java + jsp），对接PAuth使用oauth 2.0 user password grant授权方式实现用户登录功能。


## 简介
本项目是一个简单的java web应用，有一个简单的用户登录页面。本项目主要演示如何对接PAuth，使用oauth 2.0 user password grant授权方式实现用户登录功能。

整个登录过程无web的重定向过程，但是可以看到应用可以获取到用户名和密码，所以这种授权方式适用于高可信的web应用中。

## 项目的结构和构建
```
- pom.xml Maven项目构建文档
- README.md 使用说明文档
+ src/main/java
  + com.ppdai.auth.sample
    - DemoApp 演示主程序，通过resource owner credential向授权服务器获取访问令牌，并存储在web cookie中
    - DemoProperties
+ src/main/resources
  + application.properties 项目配置，包括授权服务器地址、应用所注册的id/secret
  + static
    - index.html 首页
    - login.html 登录页面
    - logout.html 注销登录页面
```

在pom.xml项目中添加了如下对pauth-spring-filter的依赖，
``` xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-filter</artifactId>
    <version>${pauth.version}</version>
</dependency>
```
这个依赖将给当前web应用引入pauth filter，关于这个pauth-filter更多使用方法请参见如下项目的README文件，
- auth-client/auth-spring-filter/README.md

构建命令：mvn compile package
启动命令：java -jar ./target/simple-java-web-1.1.0.jar

## 演示步骤

1. 准备工作：启动pauth server
   - 请根据auth-server项目的README文档，启动pauth server
   - 若根据项目的缺省配置，pauth server将启动在如下地址
   ```
   http://localhost:8090
   ```

2. 准备工作：注册应用，获取client id/secret
   - 请根据auth-front项目的README文档，启动pauth前端管理页面
   - 若根据项目的缺省配置，pauth front将启动在如下地址
   ```
   http://localhost
   ```
   - 打开前端管理页面，输入用户名和密码登录。用户名和密码的账号信息请参考auth-front项目的README文档.
   - 登录成功后，注册应用，获取client id/secret

3. 演示项目配置
   - 进入到项目配置文件 src/main/resources/application.properties
   - 配置授权登录服务器地址，Client id和secret信息如下
   ```
   # pauth spring filter settings
   pauth.api.url = http://localhost:8090

   # pauth client settings
   pauth.client.id = demo
   pauth.client.secret = zds1fT
   ```

4. 演示步骤
   - 启动演示应用，根据项目缺省配置，应用将启动在如下地址，
   ```
   http://localhost:8100
   ```
   - 访问首页地址 http://localhost:8100
   - 在首页点击“获取用户信息”按钮，将弹出“无法获取用户信息，请先登录”的提示框。
   - 点击登录页面的链接，跳转到登录页面，输入用户名和密码，若一切正常，则将弹出“登录成功”的提示框。若出现问题，则会提示“登录失败”的信息。
   - 回到首页，重新点击“获取用户信息”按钮，将弹出“当前登录用户是test”的提示框。
   - 点击注销登录页面的链接，跳转到注销登录页面。点击“登出”按钮，将弹出“已成功登出”的提示框。
   - - 回到首页，再次点击“获取用户信息”按钮，将提示用户未登录信息。
