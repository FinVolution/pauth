## 1. 简介

本项目实现了OAuth 2.0的四种授权模式，用于应用的单点登录、按领域进行划分的用户+角色+应用、资源访问的权限控制等各个功能。提供相应的客户端、java filter、网关组件，实现应用的分布式权限控制和集中式权限控制。并提供样例，演示在纯前端项目，后端web服务，前后端分离项目，及其纯后端应用服务项目等各种项目类型，如何集成PAuth实现相关功能。

## 2. 为什么有这个项目

OAuth 2.0作为一个业界的授权代理模式，已经有广泛的应用。其在业界有很多较好的实现，无论是spring security/shiro，还是keycloak/gravitee/mitreid connect等等，都是非常不错并值得借鉴的授权应用框架。但是，我们希望在如下方面能够有更好的功能支持，

- 简单的技术框架，更好的扩展点（identity provider/token issue/security control），支持高可用、高并发场景
- 可以应用于纯前端项目、纯后端项目、后端Web项目、前后端分离项目，提供最佳实践
- 提供简单的java客户端、spring filter、网关组件，实现应用的分布式权限控制和集中式权限控制
- 颁发Jwt Token，里面的信息内容可以自定义填充和解析，支持单向解密

这也是这个项目的源起。

更多授权应用框架的功能对比见下面。

## 3. 和其它授权应用框架的比较

|  | keycloak | gravitee | spring security  | mitreid connect | PAuth |
| ---:  | ---  |  --- | ---   | :---  | :---  |
| 授权方式 | oidc + oauth2 + saml + exchange | oidc + oauth2 | oauth2 | oidc + oauth2 | oidc + oauth2 |
| Token的颁发实现 |  自定义  | Spring Authorization Server + 自定义oidc实现 | Spring Authorization Server | Spring Authorization Server + 自定义token颁发 |  自定义token颁发 + 自定义oidc实现  |
| Token类别 |  JWT  | JWT | UUID | JWT |  JWT |
| Token单向解密 |  支持? | 不支持 | 不支持 | 不支持 | 支持 |
| 权限控制  | java web filter | java web filter | spring filter chain | spring filter chain | spring filter chain |
| 认证源 |   ldap + db + social(oauth2)   |  ldap + db + social(oauth2)  |  ldap + db + social(oauth2)  | 无 |  ldap + db + social(oauth2)  |
| 扩展插件机制  |  java spi   | 自定义spi | spring aop + filter configurer | 无插件扩展? | spring bean |
| 客户端支持  | 前端+后端(丰富的adapter) | 无 | spring security - resource server |  自定义 | spring security - resource server  |
| 是否有域控 | 有（realm）  |  有（domain） | 无  | 无  | 有（domain） |
| 主要应用场景 | 单点登录（adapter），用户访问控制  |  api访问控制 | 单纯的oauth2  | 单纯的oidc+oauth2  | 单点登录和api访问控制 |


表格中，
- OIDC是OpenID Connect的简称，由OpenID Foundation发布的一个基于OAuth 2.0上的认证开放标准，其包含一个核心的规范和多个可选的规范实现。
- oauth2是指OAuth 2.0的授权框架，其主要定义了四种授权方式的规范实现。
- Spring Authorization Server是指Spring Security OAuth2.0提供的OAuth2.0服务器实现。
- ?是指该项还待后续考察

## 4. 一分钟入门

TBD

## 5. 项目的结构和构建

### 5.1 下载项目代码
使用git命令下载项目代码，
```
git clone pauth
```

主要有如下三种分支，
- 主分支：master
- 开发分支：dev
- 发布分支：根据版本号命名

### 5.2 项目代码结构

```
- README.md
- LICENSE
- CODE-OF-CONDUCT.md
- HOW-TO-CONTRIBUTE.md
+ auth-common （公共类库）
+ auth-admin （后端管理服务）
+ auth-server OAuth2授权应用服务
+ auth-front 前端访问页面
+ auth-client 客户端、提供PAuth的Java Client和Spring Fitler等对接类库，方便应用接入
  + auth-java-client 访问api接口
  + auth-java-filter 分布式权限控制（filter）
  + auth-spring-filter 分布式权限控制（filter）
  + auth-spring-support 提供更多spring bean，方便token的刷新、吊销等操作
  + auth-gateway 通过Spring Zuul实现简单的集中式权限控制网关
+ sample 演示项目
  + simple-front-jquery (implicit grant mode + jquery) 纯前端项目，对接PAuth，使用OAuth2.0 implicit Grant授权方式
  + simple-front-vue (authorization code + vue ) 纯前端项目，使用vue框架，后端使用express服务器，使用OAuth2.0 Authorization Code Grant授权方式
  + simple-java-web (user password + spring web) 使用spring web开发一个web服务，后端提供静态页面，使用OAuth2.0 Resource Owner Credential Grant授权方式
  + simple-spring-web (authorization code + spring web) 前后端分离，前端使用vue框架，后端使用spring boot开发web服务，使用OAuth2.0 Authorization Code Grant授权方式
  + simple-java-client (client credential + spring web) 纯后端项目，使用OAuth2.0 Client Credential Grant授权方式
    + resource-server 资源服务器
    + resource-client 访问资源服务器的一个应用
+ docs 项目文档
  - arch design 项目架构
  - features 项目功能介绍
  - guide/how to use 用户手册
  - sample introduction 使用样例
  - ppt 相关PPT演示文档
```

#### 演示样例项目

| 样例项目 | 前端框架 | 后端框架 | OAuth2.0授权方式 |
| --- | --- | --- | --- |
| simple-front-jquery | 纯前端 | 无 | Implicit Grant |
| simple-front-vue | 前端vue框架 + express服务器 | 无 | Authorization Code Grant |
| simple-java-web | 无 | 后端spring框架 + 后端静态登录页面 | Resource Owner Credential Grant |
| simple-java-client | 无 | 后端spring框架 + 后端静态页面 | Client Credential Grant |
| simple-spring-web | 前端vue框架 + express服务器|后端spring框架 |Authorization Code Grant |

### 5.3 项目构建和运行环境

下面为各个项目构建和运行的简要描述，具体可以参考各个子项目的README.md文件

1. 前端：Nodejs 6.9.1
   - 前端构建命令为，
   ``` bash
   npm run build
   ```
   - 前端运行命令为，
   ``` bash
   npm run dev
   ```
2. 后端：JDK 8 + Maven 3.3.9 + Mysql
   - 后端构建命令为，
   ``` bash
   mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
   mvn clean package
   ```
   - 后端运行命令为（参考样例，具体jar包的名字和版本根据子项目而变化），
   ``` bash
   java -jar pauth-0.0.1-SNAPSHOT.jar
   ```
   - 注：若使用JDK 9，建议使用Maven 3.5以上版本进行构建。

## 6. 下一步

请进入各个子项目，阅读提供README文件来进行项目的构建和开发运行

## 7. 联系
我们的邮箱地址：framework@xxx.com，欢迎来信联系。

## 8. 开源许可协议
Apache License 2.0
