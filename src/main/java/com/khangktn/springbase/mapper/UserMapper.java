package com.khangktn.springbase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.request.UserUpdateRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.entity.User;

@Mapper(componentModel = "spring") // Tell Spring that: This mapper used in spring, it will support ID for us
public interface UserMapper {
    User toUser(UserCreationRequest request); 

    @Mapping(target = "lastName", ignore = true)
    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
