package com.guilinares.clinicai.config;

import com.guilinares.clinicai.zapi.ZApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZApiProperties.class)
public class AppConfig {
}
