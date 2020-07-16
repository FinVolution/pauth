
这是一个演示项目，使用Java开发一个web程序，对接PAuth，使用oauth 2.0 client credentials grant授权模式，完成client对resource server API的授权调用。

## 简介

对于一个纯后端java web应用来说，其可能本身就是资源的所有者，或者其无法提供任何UI界面让用户输入用户名和密码登录的方式来进行授权，对于这种应用场景，OAuth2.0提供了client credentials grant授权模式。

在client credentials grant授权模式下，无须用户的认证授信，只需要client提供client id/secret，即可获得授权令牌，用于访问资源服务器API。

本项目主要演示这个授权过程，项目中有两个子项目，

- resource client 资源客户端应用
- resource server 资源服务器，其API接口受到过滤器的授权保护

资源客户端应用在向资源服务器发起请求之前，将通过自己的授信（client id/secret）向PAuth授权服务器获得授权访问令牌（access token），然后把令牌放入请求header中，以使得可以成功调用资源服务器的API，获得相应的信息。

## 项目的结构和构建

```
- pom.xml Maven项目构建文档
- README.md 使用说明文档
+ resource-client  资源客户端应用
  - pom.xml
  + src/main/java
    - ResourceClient
  + src/main/resources
    - application.properties
+ resource-server 资源服务器，受到授权保护
  - pom.xml
  + src/main/java
    - ResourceServer
  + src/main/resources
    - application.properties
```

#### 项目依赖配置 - resource server
在项目resource-server中添加了如下的依赖，
``` xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-filter</artifactId>
    <version>${pauth.version}</version>
</dependency>
```
这个依赖将给当前web应用添加一个filter，关于这个filter更多使用方法请参见项目README文件。
- auth-client/auth-spring-filter/README.md

#### 项目依赖配置 - resource client
在项目resource-client中添加了如下的依赖，
``` xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-client</artifactId>
    <version>${pauth.version}</version>
</dependency>
```
这个依赖库将会为当前spring web项目实现pauth client的自动化注入，

``` java
@Autowired
private OAuth2EndpointApi pAuthApi;
```

关于这个client更多使用方法请参见项目README文件。
- auth-client/auth-spring-client/README.md

#### 项目构建

- 构建命令：mvn compile package
- 启动命令：java -jar ./target/simple-java-web-1.1.0.jar

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

3. 启动演示项目resource server
   - 进入到项目配置文件 src/main/resources/application.properties
   - 配置授权登录服务器地址和过滤器，对login接口之外的所有请求进行访问检查

   ```
   # pauth spring filter settings
   pauth.api.url = http://localhost:8090

   # pauth spring filter settings
   pauth.spring.filter.type = all-check-by-skip
   pauth.spring.filter.token.store.type = header
   pauth.spring.filter.token.name = resource-token
   pauth.spring.filter.special.urls = /login
   ```

   - 启动演示项目resource server，其地址为：http://localhost:8200
   - 在浏览器上打开 [http://localhost:8200/login](http://localhost:8200/login)，将正常获取接口返回信息
   - 在浏览器上打开 [http://localhost:8200/hello](http://localhost:8200/hello)，将无法访问接口，收到400的错误响应。这是由于hello接口已经受到pauth filter保护，需要请求中带有合法的访问令牌才能正常访问该接口。

4. 启动演示项目resource client
   - 进入到项目配置文件 src/main/resources/application.properties
   - 配置授权登录服务器地址，Client id和secret信息如下

   ```
   # pauth spring filter settings
   pauth.api.url = http://localhost:8090

   # pauth client settings
   pauth.client.id = demo
   pauth.client.secret = zds1fT
   ```

   - 启动演示项目resource client，其地址为：http://localhost:8300
   - 在浏览器上打开 [http://localhost:8300/hello](http://localhost:8300/hello)，该接口将会先向授权服务器获取访问令牌，然后访问resource server服务器受保护的hello接口，并将响应信息返回。
