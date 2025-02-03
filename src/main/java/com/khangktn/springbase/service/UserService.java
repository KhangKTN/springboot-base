package com.khangktn.springbase.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.khangktn.springbase.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.khangktn.springbase.enums.Role;
import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.request.UserUpdateRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.exception.AppException;
import com.khangktn.springbase.exception.ErrorCode;
import com.khangktn.springbase.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponse createUser(final UserCreationRequest userCreationRequest){
        if(userRepository.existsByUsername(userCreationRequest.getUsername()))
            throw new AppException(ErrorCode.USER_EXIST);

        final User user = userMapper.toUser(userCreationRequest);
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);
        
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(final String id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(final UserUpdateRequest request, final String userId){
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(final String userId){
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}
