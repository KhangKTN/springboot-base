package com.khangktn.springbase.controller;

import com.khangktn.springbase.dto.request.ApiResponse;
import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request){
        boolean result = authenticationService.authentication(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .authenticated(result).build())
                .build();
    }
}
