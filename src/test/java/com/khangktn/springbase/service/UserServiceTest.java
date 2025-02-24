package com.khangktn.springbase.service;

import com.khangktn.springbase.dto.request.UserCreationRequest;
import com.khangktn.springbase.dto.response.UserResponse;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.exception.AppException;
import com.khangktn.springbase.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

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
        final UserResponse response = userService.createUser(userCreationRequest);

        // THEN
        Assertions.assertEquals(response.getId(), userResponse.getId());
        Assertions.assertEquals(response.getUsername(), userResponse.getUsername());
        Assertions.assertEquals(response.getFirstName(), userResponse.getFirstName());
        Assertions.assertEquals(response.getLastName(), userResponse.getLastName());
    }

    @Test
    void createUser_userExists_fail() {
        // GIVEN
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(true);

        // THEN
        final AppException appException = Assertions.assertThrows(AppException.class,
                () -> userService.createUser(userCreationRequest));
        Assertions.assertEquals(appException.getMessage(), "User is exist!");
        Assertions.assertEquals(appException.getErrorCode().getCode(), 1002);
    }
}
