package com.khangktn.springbase.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khangktn.springbase.dto.request.ApiResponse;
import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.request.LogoutRequest;
import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.request.RefreshTokenRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.dto.response.ObserveResponse;
import com.khangktn.springbase.dto.response.RefreshTokenResponse;
import com.khangktn.springbase.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authentication(@RequestBody final AuthenticationRequest request){
        final AuthenticationResponse authenticationResp = authenticationService.authentication(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationResp)
                .build();
    }

    @PostMapping("/observe")
    public ApiResponse<ObserveResponse> observeToken(@RequestBody final ObserveRequest observeRequest) {
        final ObserveResponse observeResponse = authenticationService.observe(observeRequest);
        return ApiResponse.<ObserveResponse>builder()
            .result(observeResponse)
            .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody final LogoutRequest logoutRequest) {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder().build();
    }
    
    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> getNewToken(@RequestBody final RefreshTokenRequest request) {
        final RefreshTokenResponse response = RefreshTokenResponse.builder()
                .token(authenticationService.refreshToken(request))
                .build();
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(response)
                .build();
    }
}
