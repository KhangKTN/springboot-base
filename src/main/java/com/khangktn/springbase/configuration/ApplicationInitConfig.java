package com.khangktn.springbase.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.enums.Role;
import com.khangktn.springbase.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            final boolean hasUserRoleAdmin = userRepository.findByUsername("ADMIN").isPresent();

            if (!hasUserRoleAdmin) {
                final Set<String> roles = new HashSet<>();
                roles.add(Role.ADMIN.name());

                final User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created wih password: 'admin'!");
            }
        };
    }
}
