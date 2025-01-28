package com.khangktn.springbase.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khangktn.springbase.dto.request.ApiResponse;
import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.dto.response.ObserveResponse;
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
    ApiResponse<AuthenticationResponse> authentication(final @RequestBody AuthenticationRequest request){
        final AuthenticationResponse authenticationResp = authenticationService.authentication(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationResp)
                .build();
    }

    @PostMapping("/observe")
    public ApiResponse<ObserveResponse> observeToken(final @RequestBody ObserveRequest observeRequest) {
        final ObserveResponse observeResponse = authenticationService.observe(observeRequest);
        return ApiResponse.<ObserveResponse>builder()
            .result(observeResponse)
            .build();
    }
}
