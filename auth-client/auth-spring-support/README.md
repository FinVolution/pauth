## 简介

这是一个spring support项目，集成了pauth-spring-filter，通过校验请求中的访问令牌实现分布式权限控制，同时自身提供了获取、刷新、吊销token等方法以及解析token的工具类，方便各个应用程序对接PAuth。

## 项目要求

- java 8+
- spring boot 1.5.8+

## 类库引入

对于Maven项目，需要在pom.xml文件中添加如下对pauth-spring-support的依赖类库，

```xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-support</artifactId>
    <version>${pauth.version}</version>
</dependency>
```

其中pauth.version为项目版本，建议使用最新版本。

## 配置

在项目的配置文件中，添加如下的配置，

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

各个配置含义和参数值说明如下，
- pauth.api.url pauth的前端地址，发送过来的请求会自动转发到后端
- pauth.spring.filter.type 过滤器类型，有all-skip-by-check和all-check-by-skip两种类型，配合特殊urls一起工作，注意：该配置项为空的话，过滤器不会初始化，不会对任何请求进行过滤
  * all-skip-by-check 跳过所有请求，但检查指定的特殊urls
  * all-check-by-skip 检查所有请求，但跳过指定的特殊urls
- pauth.spring.filter.token.store.type 令牌的存储类型，可选项为header或cookie，若不配置此项则默认为header
- pauth.spring.filter.token.name 令牌在header/cookie中的键名，若不配置此项则默认为pauth-token
- pauth.spring.filter.special.urls 用来检查或跳过的特殊urls，url之间用英文逗号分隔，每条url支持正则表达式，可以用&符号绑定请求类型，以下是使用样例
  * .* 所有请求
  * /test.* 以/test开头的所有请求
  * GET&.* 所有GET请求
  * POST&/test 路径为/test的POST请求
  * GET\&.\*,PUT\&.\* 所有GET和PUT请求
- pauth.api.clientId 在pauth系统上注册的client名
- pauth.api.redirectUri client用来接收授权码的地址，与注册client时填写的重定向返回地址匹配才能进行授权
- pauth.api.authorization 注册client时生成的Basic Auth字符串，在获取、刷新和吊销token时起校验作用

## 注意事项

对于检查的请求，根据指定的配置获取cookie/header中的令牌，
- 若令牌为空，则返回400（BAD_REQUEST）的错误响应
- 若令牌合法有效，则允许访问
- 若令牌不合法或者已失效，则返回401（UNAUTHORIZED）的错误响应

调用PauthTokenService中的方法前需要使用@Autowired注解装配Bean，

```
@Autowired
private PauthTokenService pauthTokenService;
```

PauthTokenUtil同理，

```
@Autowired
private PauthTokenUtil pauthTokenUtil;
```