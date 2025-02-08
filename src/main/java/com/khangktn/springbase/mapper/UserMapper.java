package com.khangktn.springbase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.request.UserUpdateRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.entity.User;

@Mapper(componentModel = "spring") // Tell Spring that: This mapper used in spring, it will support DI for us
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserCreationRequest request); 

    @Mapping(target = "lastName", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
