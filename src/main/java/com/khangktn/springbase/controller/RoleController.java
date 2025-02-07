package com.khangktn.springbase.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khangktn.springbase.dto.request.ApiResponse;
import com.khangktn.springbase.dto.request.RoleRequest;
import com.khangktn.springbase.dto.response.RoleResponse;
import com.khangktn.springbase.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;



@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody final RoleRequest roleRequest) {
        final RoleResponse roleResponse = roleService.create(roleRequest);
        final ApiResponse<RoleResponse> response = ApiResponse.<RoleResponse>builder()
                .result(roleResponse)
                .build();
        return response;
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        final List<RoleResponse> roleResponseList = roleService.getAll();
        final ApiResponse<List<RoleResponse>> response = ApiResponse.<List<RoleResponse>>builder()
                .result(roleResponseList)
                .build();
        return response;
    }
    
    @DeleteMapping("/{name}")
    public ApiResponse<Boolean> delete(@PathVariable("name") final String name) {
        final boolean isDeleteSuccess = roleService.delete(name);
        return ApiResponse.<Boolean>builder()
                .result(isDeleteSuccess)
                .build();
    }
}
