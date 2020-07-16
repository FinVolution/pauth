这是一个spring java filter，用于各个应用程序集成对接PAuth，校验请求中的访问令牌（通过cookie/header获取），实现分布式权限控制。

## 项目要求

- java 8+
- spring boot 1.5.8+

## 类库引入
对于Maven项目，在pom.xml项目中添加了如下对pauth-spring-filter的依赖类库，

``` xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-filter</artifactId>
    <version>${pauth.version}</version>
</dependency>
```

其中pauth.version为项目版本，建议使用最新版本。

## 配置

在项目的配置文件中，添加如下的配置，

```
# pauth spring filter settings - remote api
pauth.api.url = http://localhost:8090

# pauth spring filter settings
pauth.spring.filter.type = all-skip-by-check
pauth.spring.filter.special.urls = .*
pauth.spring.filter.token.store.type = header
pauth.spring.filter.token.name = resource-token
```

各个配置含义和参数值说明如下，
- pauth.api.url 为远程PAuth Server的API地址
- pauth.spring.filter.type 过滤器类型，有all-skip-by-check和all-check-by-skip两种类型，配合特殊urls一起工作。该配置项为空的话，则过滤器不会初始化。
  * all-skip-by-check 跳过所有请求，但检查指定的特殊urls
  * all-check-by-skip 检查所有请求，但跳过特殊指定的urls
- pauth.spring.filter.special.urls 特殊urls，多个URL可以通过逗号分开
- pauth.spring.filter.token.store.type 为令牌在请求中的存储类型，有header/cookie两种类型
- pauth.spring.filter.token.name 令牌在header/cookie的键名

特殊URLs的使用样例，
- .*  所有请求
- /test.* 以test开头命名的所有请求
- GET\&.* 所有GET请求
- GET\&.\*,PUT\&.\* 所有GET和PUT请求

对于检查的请求，根据指定的配置获取cookie/header中的令牌，
- 若令牌为空，否则返回400（BAD_REQUEST）的错误响应
- 若令牌合法有效，则允许访问
- 若令牌不合法或者已失效，则返回401（UNAUTHORIZED）的错误响应

注意的是，即使引入了filter类库，若没有通过pauth.spring.filter.type指定过滤器类型，则过滤器不会被初始化，不会对任何请求进行过滤。
