package org.changppo.account.oauth2.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.oauth2.OAuth2Properties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.changppo.account.type.OAuth2Type.OAUTH2_KAKAO;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2Client extends OAuth2Client {

    private final RestTemplate restTemplate;
    private final OAuth2Properties oauth2Properties;

    @Override
    protected String getSupportedProvider() {
        return OAUTH2_KAKAO.name();
    }

    @Override
    public void unlink(String MemberId) {
        try{
        HttpEntity<String> request = createRequest(MemberId);
        ResponseEntity<String> response = restTemplate.exchange(KakaoConstants.KAKAO_UNLINK_URL, HttpMethod.POST, request, String.class);
        handleResponse(response);
        } catch (Exception e) {
            log.error("Failed to unlink Kakao account.", e);
        }
    }

    private HttpEntity<String> createRequest(String providerMemberId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, oauth2Properties.getKakao().getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = String.format(KakaoConstants.KAKAO_UNLINK_REQUEST_BODY_FORMAT, providerMemberId);
        return new HttpEntity<>(body, headers);
    }

    private void handleResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to unlink Kakao account.");
        }
    }
}
