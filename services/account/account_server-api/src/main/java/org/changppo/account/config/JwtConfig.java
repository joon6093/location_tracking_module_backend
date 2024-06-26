package org.changppo.account.config;

import org.changppo.utils.jwt.JwtProperties;
import org.changppo.utils.jwt.apikey.ApiKeyJwtHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public ApiKeyJwtHandler apiKeyJwtHandler(JwtProperties jwtProperties) {
        return new ApiKeyJwtHandler(jwtProperties);
    }
}
