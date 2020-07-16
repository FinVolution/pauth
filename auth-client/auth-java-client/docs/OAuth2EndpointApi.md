# OAuth2EndpointApi

Method | HTTP request | Description
------------- | ------------- | -------------
[**authorizeUsingPOST**](OAuth2EndpointApi.md#authorizeUsingPOST) | **POST** /oauth2/authorize | OAuth2授权点
[**introspectTokenUsingPOST**](OAuth2EndpointApi.md#introspectTokenUsingPOST) | **POST** /oauth2/introspect | OAuth2令牌检查点
[**issueTokenUsingPOST1**](OAuth2EndpointApi.md#issueTokenUsingPOST1) | **POST** /oauth2/token | OAuth2令牌颁发点
[**revokeTokenUsingPOST**](OAuth2EndpointApi.md#revokeTokenUsingPOST) | **POST** /oauth2/revoke | OAuth2令牌吊销点


<a name="authorizeUsingPOST"></a>
# **authorizeUsingPOST**
> AuthCodeVO authorizeUsingPOST(clientVO)

OAuth2授权点

授权点颁发authorization code

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.OAuth2EndpointApi;


OAuth2EndpointApi apiInstance = new OAuth2EndpointApi();
ClientVO clientVO = new ClientVO(); // ClientVO | clientVO
try {
    AuthCodeVO result = apiInstance.authorizeUsingPOST(clientVO);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling OAuth2EndpointApi#authorizeUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientVO** | [**ClientVO**](ClientVO.md)| clientVO |

### Return type

[**AuthCodeVO**](AuthCodeVO.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="introspectTokenUsingPOST"></a>
# **introspectTokenUsingPOST**
> ValidityVO introspectTokenUsingPOST(token)

OAuth2令牌检查点

检查令牌

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.OAuth2EndpointApi;


OAuth2EndpointApi apiInstance = new OAuth2EndpointApi();
String token = "token_example"; // String | token
try {
    ValidityVO result = apiInstance.introspectTokenUsingPOST(token);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling OAuth2EndpointApi#introspectTokenUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **String**| token |

### Return type

[**ValidityVO**](ValidityVO.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="issueTokenUsingPOST1"></a>
# **issueTokenUsingPOST1**
> OAuth2AccessToken issueTokenUsingPOST1(grantType, authorization, code, redirectUri, refreshToken, username, password)

OAuth2令牌颁发点

颁发令牌access/refresh token

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.OAuth2EndpointApi;


OAuth2EndpointApi apiInstance = new OAuth2EndpointApi();
String grantType = "grantType_example"; // String | grant_type
String authorization = "authorization_example"; // String | Authorization
String code = "code_example"; // String | code
String redirectUri = "redirectUri_example"; // String | redirect_uri
String refreshToken = "refreshToken_example"; // String | refresh_token
String username = "username_example"; // String | username
String password = "password_example"; // String | password
try {
    OAuth2AccessToken result = apiInstance.issueTokenUsingPOST1(grantType, authorization, code, redirectUri, refreshToken, username, password);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling OAuth2EndpointApi#issueTokenUsingPOST1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **grantType** | **String**| grant_type |
 **authorization** | **String**| Authorization | [optional]
 **code** | **String**| code | [optional]
 **redirectUri** | **String**| redirect_uri | [optional]
 **refreshToken** | **String**| refresh_token | [optional]
 **username** | **String**| username | [optional]
 **password** | **String**| password | [optional]

### Return type

[**OAuth2AccessToken**](OAuth2AccessToken.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="revokeTokenUsingPOST"></a>
# **revokeTokenUsingPOST**
> Boolean revokeTokenUsingPOST(token, authorization)

OAuth2令牌吊销点

吊销令牌

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.OAuth2EndpointApi;


OAuth2EndpointApi apiInstance = new OAuth2EndpointApi();
String token = "token_example"; // String | token
String authorization = "authorization_example"; // String | Authorization
try {
    Boolean result = apiInstance.revokeTokenUsingPOST(token, authorization);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling OAuth2EndpointApi#revokeTokenUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **String**| token |
 **authorization** | **String**| Authorization | [optional]

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

