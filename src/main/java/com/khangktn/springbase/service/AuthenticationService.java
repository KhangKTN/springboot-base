package com.khangktn.springbase.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.dto.response.ObserveResponse;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.exception.AppException;
import com.khangktn.springbase.exception.ErrorCode;
import com.khangktn.springbase.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    // After 1 day, token will expire
    private static final int EXPIRATION_TIME_SECOND = 1 * 60 * 60;

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    /**
     * Authentication user
     * @param request
     * @return 
     */
    public AuthenticationResponse authentication(final AuthenticationRequest request) {
        final String username = request.getUsername();
        final User userDb = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        final boolean authenticated = passwordEncoder.matches(request.getPassword(), userDb.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        final String accessToken = generateToken(username);

        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .token(accessToken)
                .build();
    }

    /**
     * @param username
     * @return
     */
    private String generateToken(String username) {
        final JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("khangktn") // Domain
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(EXPIRATION_TIME_SECOND).toEpochMilli()))
                .claim("userId", "custom")
                .build();
        final Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        final JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify token
     * @param observeRequest
     * @return
     */
    public ObserveResponse observe(final ObserveRequest observeRequest) {
        final String token = observeRequest.getToken();
        try {
            final SignedJWT signedJWT = SignedJWT.parse(token);
            final JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

            final boolean isVerified = signedJWT.verify(jwsVerifier);
            final Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            final boolean isExprired = expiredTime.before(new Date());

            return ObserveResponse.builder()
                    .isValid(isVerified && !isExprired)
                    .build();
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
