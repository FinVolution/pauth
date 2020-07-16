本项目实现了pauth client在spring项目中自动化配置。

## 项目要求

- java 8+
- spring boot 1.5.8+ 

## 类库引入
对于Maven项目，在pom.xml项目中添加了如下对pauth-spring-filter的依赖类库，

``` xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-spring-client</artifactId>
    <version>${pauth.version}</version>
</dependency>
```

其中pauth.version为项目版本，建议使用最新版本。

## 配置

在项目的配置文件中，添加如下的配置，这个将指向PAuth Server的运行实例地址，

```
pauth.api.url=http://localhost:8090
```

然后就可以通过spring自动装配的方式获取PAuth client实例，

```java
@Autowired
private OAuth2EndpointApi pAuthApi;
```

接下来可以使用pAuthApi来访问远程PAuth Server的接口。
