package com.khangktn.springbase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.khangktn.springbase.dto.request.RoleRequest;
import com.khangktn.springbase.dto.response.RoleResponse;
import com.khangktn.springbase.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissionSet", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}
