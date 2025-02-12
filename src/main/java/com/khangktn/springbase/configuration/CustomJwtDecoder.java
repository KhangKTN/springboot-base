package com.khangktn.springbase.configuration;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.response.ObserveResponse;
import com.khangktn.springbase.service.AuthenticationService;
import com.nimbusds.jose.JWSAlgorithm;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomJwtDecoder implements JwtDecoder {
    final AuthenticationService authenticationService;

    NimbusJwtDecoder nimbusJwtDecoder = null;

    @Value("${jwt.signerKey}")
    String signerKey;

    @Override
    public Jwt decode(final String token) throws JwtException {
        // Verify token is valid
        final ObserveRequest observeRequest = ObserveRequest.builder()
                .token(token)
                .build();
        final ObserveResponse observeResponse = authenticationService.observe(observeRequest);
        if (!observeResponse.isValid()) {
            throw new JwtException("Token invalid!");
        }

        // Decode token
        if (Objects.isNull(nimbusJwtDecoder)) {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), JWSAlgorithm.HS512.getName());
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
