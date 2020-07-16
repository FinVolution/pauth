package com.ppdai.auth.sample;

import com.ppdai.auth.common.constant.GrantType;
import com.ppdai.auth.common.utils.BasicAuthUtil;
import com.ppdai.pauth.client.ApiException;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import com.ppdai.pauth.client.model.OAuth2AccessToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Please add description here.
 *
 */
@SpringBootApplication
@RestController
public class ResourceClient {
    final Logger logger = LoggerFactory.getLogger(ResourceClient.class);

    @Value("${pauth.client.id:}")
    private String clientId;
    @Value("${pauth.client.secret:}")
    private String clientSecret;
    @Value("${resource.server.url.demo:}")
    private String resourceServerUrl;

    @Autowired
    private OAuth2EndpointApi pAuthApi;

    public static void main(String[] args) {
        SpringApplication.run(ResourceClient.class, args);
    }

    @GetMapping("/hello")
    public ResponseEntity<Result> greet() throws IOException {
        Result result = new Result();

        String accessToken = null;
        try {
            // fetch access token from pauth server with client credential
            String authorization = BasicAuthUtil.encode(clientId, clientSecret);
            OAuth2AccessToken o2AccessToken = pAuthApi.issueTokenUsingPOST(GrantType.CLIENT_CREDENTIALS.name(), authorization, null, null, null, null, null);

            // visit resource server with access token
            accessToken = o2AccessToken.getAccessToken();
            logger.info("access token: " + accessToken);
        } catch (ApiException e) {
            result.code = -1;
            result.message = "receive an exception when trying to fetch access token with client credential grant way";
            logger.error(result.message, e.getMessage());
        }

        if (accessToken != null) {
            result.code = 0;
            result.message = doGet(resourceServerUrl, accessToken);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 演示：发送http get请求
     */
    public String doGet(String url, String accessToken) {
        String message = null;

        try {
            Request request = new Request.Builder().url(url)
                    .header("accept", "application/json")
                    .header("resource-token", accessToken)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            if (response != null && response.code() == 200) {
                message = response.body().string();
            } else {
                message = "error";
            }
        } catch (IOException e) {
            logger.error("failed to send post request.", e.getMessage());
        }

        return message;
    }

    /**
     * 返回结果
     */
    public class Result {
        public Integer code;
        public String message;
    }
}
