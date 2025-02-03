package com.khangktn.springbase.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid final UserCreationRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping("")
    ApiResponse<List<UserResponse>> getUsers(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: " + authentication.getName());
        authentication.getAuthorities().forEach(auth -> log.info("Role: " + auth.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") final String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@RequestBody final UserUpdateRequest request, @PathVariable("userId") final String userId){
        return userService.updateUser(request, userId);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") final String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
