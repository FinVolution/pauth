# auth-java-client
这是一个PAuth java客户端，用于对接PAuth的OAuth 2.0 Endpoing接口，包括Access/Refresh Token的获取、更新、校验等方法。

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.ppdai.auth</groupId>
    <artifactId>pauth-client</artifactId>
    <version>1.0.4</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.ppdai.auth:pauth-client:1.0.4"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/pauth-client-1.0.4.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import com.ppdai.pauth.client.*;
import com.ppdai.pauth.client.auth.*;
import com.ppdai.pauth.client.model.*;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;

import java.io.File;
import java.util.*;

public class OAuth2EndpointApiExample {

    public static void main(String[] args) {

        OAuth2EndpointApi apiInstance = new OAuth2EndpointApi();
        ClientVO clientVO = new ClientVO(); // ClientVO | clientVO
        try {
            AuthCodeVO result = apiInstance.authorizeUsingPOST(clientVO);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OAuth2EndpointApi#authorizeUsingPOST");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*OAuth2EndpointApi* | [**authorizeUsingPOST**](docs/OAuth2EndpointApi.md#authorizeUsingPOST) | **POST** /oauth2/authorize | OAuth2授权点
*OAuth2EndpointApi* | [**introspectTokenUsingPOST**](docs/OAuth2EndpointApi.md#introspectTokenUsingPOST) | **POST** /oauth2/introspect | OAuth2令牌检查点
*OAuth2EndpointApi* | [**issueTokenUsingPOST1**](docs/OAuth2EndpointApi.md#issueTokenUsingPOST1) | **POST** /oauth2/token | OAuth2令牌颁发点
*OAuth2EndpointApi* | [**revokeTokenUsingPOST**](docs/OAuth2EndpointApi.md#revokeTokenUsingPOST) | **POST** /oauth2/revoke | OAuth2令牌吊销点


## Documentation for Models

 - [AuthCodeVO](docs/AuthCodeVO.md)
 - [ClientVO](docs/ClientVO.md)
 - [OAuth2AccessToken](docs/OAuth2AccessToken.md)
 - [OAuth2RefreshToken](docs/OAuth2RefreshToken.md)
 - [ValidityVO](docs/ValidityVO.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author
