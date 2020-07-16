package com.ppdai.auth.config;

import com.ppdai.atlas.client.api.UserControllerApi;
import com.ppdai.atlas.client.api.UserRoleControllerApi;
import com.ppdai.atlas.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasBeanConfig {
    @Value("${atlas.api.url}")
    private String atlasUrl;

    @Value("${atlas.api.token}")
    private String atlasToken;

    @Value("${atlas.api.connTimeout}")
    private Integer atlasConnTimeout;

    @Value("${atlas.api.readTimeout}")
    private Integer atlasReadTimeout;

    @Autowired
    private ApiClient atlasApiClient;

    @Bean
    public ApiClient atlasApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(atlasUrl);
        apiClient.setConnectTimeout(atlasConnTimeout);
        apiClient.setReadTimeout(atlasReadTimeout);
        apiClient.addDefaultHeader("jwt-token", atlasToken);
        return apiClient;
    }

    @Bean
    public UserControllerApi atlasUserControllerApi() {
        UserControllerApi userControllerApi = new UserControllerApi(atlasApiClient);
        return userControllerApi;
    }

    @Bean
    public UserRoleControllerApi atlasUserRoleControllerApi() {
        UserRoleControllerApi userRoleControllerApi = new UserRoleControllerApi(atlasApiClient);
        return userRoleControllerApi;
    }
}
