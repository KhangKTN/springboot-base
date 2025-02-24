package com.khangktn.springbase.configuration;

import com.khangktn.springbase.entity.Role;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.enums.RoleEnum;
import com.khangktn.springbase.repository.RoleRepository;
import com.khangktn.springbase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    /**
     * Init default a user with role Admin if isn't exists
     *
     * @param userRepository DI constructor userRepository
     * @return arguments function
     * {@code @ConditionalOnProperty} Use for config only run when not Test enviroment
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            final UserRepository userRepository,
            final RoleRepository roleRepository
    ) {
        return args -> {
            final boolean hasUserRoleAdmin = userRepository.findByUsername("ADMIN").isPresent();

            if (!hasUserRoleAdmin) {
                final Set<Role> roles = new HashSet<>();
                final Optional<Role> roleOptional = roleRepository.findById(RoleEnum.ADMIN.name());
                roles.add(roleOptional.orElse(null));

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
