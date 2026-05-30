package com.example.grpcclient.config;

import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GrpcExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGrpcError(StatusRuntimeException ex) {
        HttpStatus httpStatus = switch (ex.getStatus().getCode()) {
            case NOT_FOUND       -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS  -> HttpStatus.CONFLICT;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAVAILABLE     -> HttpStatus.SERVICE_UNAVAILABLE;
            default              -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(httpStatus).body(Map.of(
                "error",   ex.getStatus().getCode().name(),
                "message", ex.getStatus().getDescription() != null
                        ? ex.getStatus().getDescription()
                        : ex.getMessage()
        ));
    }
}