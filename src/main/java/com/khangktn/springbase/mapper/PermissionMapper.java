package com.khangktn.springbase.mapper;


import org.mapstruct.Mapper;

import com.khangktn.springbase.dto.request.PermissionRequest;
import com.khangktn.springbase.dto.response.PermissionResponse;
import com.khangktn.springbase.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permission permission);
}
