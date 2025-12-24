package com.guilinares.clinicai.zapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class ZApiClientConfig {

    @Bean
    public RestClient zapiRestClient(ZApiProperties props) {
        String baseUrl = props.getBaseUrl()
                + "/instances/" + props.getInstanceId()
                + "/token/" + props.getToken();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Client-Token", props.getClientToken())
                .build();
    }
}
