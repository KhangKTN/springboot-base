package com.khangktn.springbase.controller;

import com.khangktn.springbase.dto.request.ApiResponse;
import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.request.UserUpdateRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid final UserCreationRequest request) {
        log.info("createUser: {}", request);
        final ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping("")
    ApiResponse<List<UserResponse>> getUserList() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUserList())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") final String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/profile")
    ApiResponse<UserResponse> getCurrentProfile() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getCurrentProfile())
                .build();
    }


    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(
            @RequestBody final UserUpdateRequest request,
            @PathVariable("userId") final String userId
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request, userId))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") final String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
}
