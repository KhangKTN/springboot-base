package com.khangktn.springbase.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;

    @Size(min = 4, message = "PASSWORD_INVALID")
    String password;

    String firstName;

    String lastName;
    
    LocalDate dob;

    List<String> roles;
}
