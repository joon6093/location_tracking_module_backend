package org.changppo.account.controller.auth;

import lombok.RequiredArgsConstructor;
import org.changppo.account.response.Response;
import org.changppo.account.response.exception.common.ExceptionType;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.changppo.account.response.exception.common.ExceptionType.*;

@RestController
@RequiredArgsConstructor
public class LoginFailureController {

    private final ResponseHandler responseHandler;

    @GetMapping("/login")
    public ResponseEntity<Response> loginError(@RequestParam("error")  String error) {
        ExceptionType exceptionType = getErrorType(error);
        return ResponseEntity
                .status(exceptionType.getStatus())
                .body(responseHandler.getFailureResponse(exceptionType));
    }

    private ExceptionType getErrorType(String error) {
        return switch (error) {
            case "member-deletion" -> MEMBER_DELETION_REQUESTED_EXCEPTION;
            case "oauth-failure" -> OAUTH2_LOGIN_FAILURE_EXCEPTION;
            default -> AUTHENTICATION_ENTRY_POINT_EXCEPTION;
        };
    }
}
