package org.changppo.account.response.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ExceptionType {
    EXCEPTION(INTERNAL_SERVER_ERROR, "EXCEPTION.CODE", "EXCEPTION.MSG"),
    AUTHENTICATION_ENTRY_POINT_EXCEPTION(UNAUTHORIZED, "AUTHENTICATION_ENTRY_POINT_EXCEPTION.CODE", "AUTHENTICATION_ENTRY_POINT_EXCEPTION.MSG"),
    ACCESS_DENIED_EXCEPTION(FORBIDDEN, "ACCESS_DENIED_EXCEPTION.CODE", "ACCESS_DENIED_EXCEPTION.MSG"),
    BIND_EXCEPTION(BAD_REQUEST, "BIND_EXCEPTION.CODE", "BIND_EXCEPTION.MSG"),
    OAUTH2_LOGIN_FAILURE_EXCEPTION(UNAUTHORIZED, "OAUTH2_LOGIN_FAILURE_EXCEPTION.CODE", "OAUTH2_LOGIN_FAILURE_EXCEPTION.MSG"),
    MEMBER_DELETION_REQUESTED_EXCEPTION(UNAUTHORIZED, "MEMBER_DELETION_REQUESTED_EXCEPTION.CODE", "MEMBER_DELETION_REQUESTED_EXCEPTION.MSG"),
    ADMIN_BANNED_EXCEPTION(UNAUTHORIZED, "ADMIN_BANNED_EXCEPTION.CODE", "ADMIN_BANNED_EXCEPTION.MSG"),
    SESSION_EXPIRED_EXCEPTION(UNAUTHORIZED, "SESSION_EXPIRED_EXCEPTION.CODE", "SESSION_EXPIRED_EXCEPTION.MSG"),
    MEMBER_NOT_FOUND_EXCEPTION(NOT_FOUND, "MEMBER_NOT_FOUND_EXCEPTION.CODE", "MEMBER_NOT_FOUND_EXCEPTION.MSG"),
    UNSUPPORTED_OAUTH2_EXCEPTION(INTERNAL_SERVER_ERROR, "UNSUPPORTED_OAUTH2_EXCEPTION.CODE", "UNSUPPORTED_OAUTH2_EXCEPTION.MSG"),
    ROLE_NOT_FOUND_EXCEPTION(NOT_FOUND, "ROLE_NOT_FOUND_EXCEPTION.CODE", "ROLE_NOT_FOUND_EXCEPTION.MSG"),
    GRADE_NOT_FOUND_EXCEPTION(NOT_FOUND, "GRADE_NOT_FOUND_EXCEPTION.CODE", "GRADE_NOT_FOUND_EXCEPTION.MSG"),
    APIKEY_NOT_FOUND_EXCEPTION(NOT_FOUND, "APIKEY_NOT_FOUND_EXCEPTION.CODE", "APIKEY_NOT_FOUND_EXCEPTION.MSG"),
    PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION(NOT_FOUND, "PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION.CODE", "PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION.MSG"),
    CARD_NOT_FOUND_EXCEPTION(NOT_FOUND, "CARD_NOT_FOUND_EXCEPTION.CODE", "CARD_NOT_FOUND_EXCEPTION.MSG"),
    CARD_CREATE_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR, "CARD_CREATE_FAILURE_EXCEPTION.CODE", "CARD_CREATE_FAILURE_EXCEPTION.MSG"),
    KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR, "KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION.CODE", "KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION.MSG"),
    KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR, "KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION.CODE", "KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION.MSG"),
    KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION.CODE", "KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION.MSG"),
    KAKAOPAY_PAYMENT_GATEWAY_STATUS_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR, "KAKAOPAY_PAYMENT_GATEWAY_STATUS_FAILURE_EXCEPTION.CODE", "KAKAOPAY_PAYMENT_GATEWAY_STATUS_FAILURE_EXCEPTION.MSG"),
    UNSUPPORTED_PAYMENT_GATEWAY_EXCEPTION(BAD_REQUEST, "UNSUPPORTED_PAYMENT_GATEWAY_EXCEPTION.CODE", "UNSUPPORTED_PAYMENT_GATEWAY_EXCEPTION.MSG"),
    UPDATE_AUTHENTICATION_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR,"UPDATE_AUTHENTICATION_FAILURE_EXCEPTION.CODE", "UPDATE_AUTHENTICATION_FAILURE_EXCEPTION.MSG"),
    PAYMENT_NOT_FOUND_EXCEPTION(NOT_FOUND, "PAYMENT_NOT_FOUND_EXCEPTION.CODE", "PAYMENT_NOT_FOUND_EXCEPTION.MSG"),
    PAYMENT_EXECUTION_NOT_FOUND_EXCEPTION(INTERNAL_SERVER_ERROR, "PAYMENT_EXECUTION_NOT_FOUND_EXCEPTION.CODE", "PAYMENT_EXECUTION_NOT_FOUND_EXCEPTION.MSG"),
    PAYMENT_EXECUTION_FAILURE_EXCEPTION(INTERNAL_SERVER_ERROR, "PAYMENT_EXECUTION_FAILURE_EXCEPTION.CODE", "PAYMENT_EXECUTION_FAILURE_EXCEPTION.MSG");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionType(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
