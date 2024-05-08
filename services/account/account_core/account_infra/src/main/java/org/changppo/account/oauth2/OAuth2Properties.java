package org.changppo.account.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2") // TODO. @EnableConfigurationProperties 를 사용하도록 수정
public class OAuth2Properties {
    private Kakao kakao;
    @Getter
    @Setter
    public static class Kakao {
        private String adminKey;
    }
}
