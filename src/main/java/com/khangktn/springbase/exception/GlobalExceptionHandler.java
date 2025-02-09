package com.khangktn.springbase.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.khangktn.springbase.dto.request.ApiResponse;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@SuppressWarnings("rawtypes")
@Slf4j
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(final RuntimeException exception){
        final ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        log.error(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(final AppException exception) {
        final ErrorCode errorCode = exception.getErrorCode();
        final ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error(exception.getErrorCode().getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        final String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributeMap = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            // Get info from validate anotation
            final ConstraintViolation constraintViolation = exception.getBindingResult().getAllErrors().get(0)
                    .unwrap(ConstraintViolation.class);
            attributeMap = constraintViolation.getConstraintDescriptor().getAttributes();
            log.error(attributeMap.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        final String message = Objects.isNull(attributeMap) ? errorCode.getMessage()
                : replaceAttributes(errorCode.getMessage(), attributeMap);
        final ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .build();
        
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException() {
        final ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        final ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Return message with validate detail value
    private String replaceAttributes(String message, final Map<String, Object> attributeList) {
        final String minValue = String.valueOf(attributeList.get(MIN_ATTRIBUTE));
        final String maxValue = String.valueOf(attributeList.get(MAX_ATTRIBUTE));

        message = message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
        message = message.replace("{" + MAX_ATTRIBUTE + "}", maxValue);
        return message;
    }
}
