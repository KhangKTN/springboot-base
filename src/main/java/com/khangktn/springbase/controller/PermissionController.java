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
import com.khangktn.springbase.dto.request.PermissionListRequest;
import com.khangktn.springbase.dto.request.PermissionRequest;
import com.khangktn.springbase.dto.response.PermissionResponse;
import com.khangktn.springbase.service.PermissionService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permission")
@RestController
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody final PermissionRequest permissionRequest) {
        final PermissionResponse permissionResponse = permissionService.create(permissionRequest);
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionResponse)
                .build();
    }

    @PostMapping("/bulk-insert")
    ApiResponse<List<PermissionResponse>> create(@RequestBody final PermissionListRequest permissionRequestList) {
        final List<PermissionResponse> permissionResponse = permissionService.bulkInsert(permissionRequestList);
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionResponse)
                .build();
    }
    

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        final List<PermissionResponse> permissionResponseList = permissionService.getAll();
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionResponseList)
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<Boolean> delete(@PathVariable("name") final String name) {
        final boolean isDeleteSuccess = permissionService.delete(name);
        return ApiResponse.<Boolean>builder()
                .result(isDeleteSuccess)
                .build();
    }
}
