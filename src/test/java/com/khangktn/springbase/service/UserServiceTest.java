package com.khangktn.springbase.service;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.exception.AppException;
import com.khangktn.springbase.exception.ErrorCode;
import com.khangktn.springbase.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;

    @BeforeEach
    void initData() {
        userCreationRequest = UserCreationRequest.builder()
                .username("khangktn-test")
                .firstName("Khang")
                .lastName("KTN")
                .password("1234")
                .dob(LocalDate.of(2002, 11, 19))
                .build();

        userResponse = UserResponse.builder()
                .id("abcdefgh")
                .username("khangktn-test")
                .firstName("Khang")
                .lastName("KTN")
                .dob(LocalDate.of(2002, 11, 19))
                .build();

        user = User.builder()
                .id("abcdefgh")
                .username("khangktn-test")
                .firstName("Khang")
                .lastName("KTN")
                .dob(LocalDate.of(2002, 11, 19))
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(false);
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        // WHEN
        final UserResponse responseActual = userService.createUser(userCreationRequest);

        // THEN
        Assertions.assertEquals(userResponse.getId(), responseActual.getId());
        Assertions.assertEquals(userResponse.getUsername(), responseActual.getUsername());
        Assertions.assertEquals(userResponse.getFirstName(), responseActual.getFirstName());
        Assertions.assertEquals(userResponse.getLastName(), responseActual.getLastName());
    }

    @Test
    void createUser_userExists_fail() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                        .thenReturn(true);

        // THEN
        final AppException appException = Assertions.assertThrows(AppException.class,
                        () -> userService.createUser(userCreationRequest));

        Assertions.assertEquals("User is exist!", appException.getMessage());
        Assertions.assertEquals(1002, appException.getErrorCode().getCode());
    }

    @Test
    @WithMockUser(username = "khangktn-test")
    void getCurrentProfile_valid_success() {
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                        .thenReturn(Optional.of(user));
        final UserResponse userActual = userService.getCurrentProfile();
        
        Assertions.assertEquals("khangktn-test", userActual.getUsername());
        Assertions.assertEquals("Khang", userActual.getFirstName());
    }

    @Test
    @WithMockUser(username = "khangktn-test")
    void getCurrentProfile_notFoundUser_fail() {
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                        .thenReturn((Optional.ofNullable(null)));
        final AppException appException = Assertions.assertThrows(
                AppException.class,
                () -> userService.getCurrentProfile());

        Assertions.assertEquals(ErrorCode.USER_NOT_EXIST.getCode(), appException.getErrorCode().getCode());
        Assertions.assertEquals(ErrorCode.USER_NOT_EXIST.getMessage(), appException.getMessage());
    }
}
