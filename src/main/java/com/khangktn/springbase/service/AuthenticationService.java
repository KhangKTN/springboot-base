package com.khangktn.springbase.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.dto.response.ObserveResponse;
import com.khangktn.springbase.entity.Permission;
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

    private static final int ONE_DAY_SECONDS = 1 * 60 * 60;

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    /**
     * Authentication user
     * @param request AuthenticationRequest
     * @return AuthenticationResponse
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
        final String accessToken = generateToken(userDb);

        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .token(accessToken)
                .build();
    }

    /**
     * @param username User
     * @return string token
     */
    private String generateToken(User user) {
        final JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("khangktn.com") // Domain
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusSeconds(ONE_DAY_SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
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
     * Verify the token
     * 
     * @param observeRequest ObserveRequest
     * @return ObserveResponse
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

    /**
     * Build String contain list Role and Permission separated space character
     * 
     * @param user User
     * @return String value of list Role and Permission
     */
    private String buildScope(final User user) {
        final StringJoiner stringJoiner = new StringJoiner(" ");
        
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                final Set<Permission> permissionSet = role.getPermissionSet();
                if (!CollectionUtils.isEmpty(permissionSet)) {
                    permissionSet.forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }
        return stringJoiner.toString();
    }
}
