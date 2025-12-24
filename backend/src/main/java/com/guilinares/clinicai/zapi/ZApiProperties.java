package com.guilinares.clinicai.zapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "zapi")
public class ZApiProperties {

    private String baseUrl = "https://api.z-api.io";
    private String instanceId;
    private String token;
    private String clientToken;
}
