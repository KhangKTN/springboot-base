package com.khangktn.springbase.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.khangktn.springbase.dto.request.AuthenticationRequest;
import com.khangktn.springbase.dto.request.LogoutRequest;
import com.khangktn.springbase.dto.request.ObserveRequest;
import com.khangktn.springbase.dto.response.AuthenticationResponse;
import com.khangktn.springbase.dto.response.ObserveResponse;
import com.khangktn.springbase.entity.ExpiredToken;
import com.khangktn.springbase.entity.Permission;
import com.khangktn.springbase.entity.User;
import com.khangktn.springbase.exception.AppException;
import com.khangktn.springbase.exception.ErrorCode;
import com.khangktn.springbase.repository.ExpiredRepository;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;

    ExpiredRepository expiredRepository;

    static int ONE_DAY_SECONDS = 1 * 60 * 60;

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
     * Generate access token
     * 
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
                .jwtID(UUID.randomUUID().toString())
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
     * Confirm token isValid
     * 
     * @param observeRequest ObserveRequest
     * @return ObserveResponse
     */
    public ObserveResponse observe(final ObserveRequest observeRequest) {
        final String token = observeRequest.getToken();

        // If token invalid, then throw AppException
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false; 
        }

        return ObserveResponse.builder()
                .isValid(isValid)
                .build();
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

    public void logout(final LogoutRequest logoutRequest) {
        try {
            final SignedJWT signedJWT = verifyToken(logoutRequest.getToken());
            final String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            final ExpiredToken expiredToken = ExpiredToken.builder()
                    .id(jwtId)
                    .expiryTime(signedJWT.getJWTClaimsSet().getExpirationTime())
                    .build();
            expiredRepository.save(expiredToken);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Verify token and check DB if token is expired
     * 
     * @param token
     * @return signJwt
     */
    private SignedJWT verifyToken(final String token) {
        try {
            final SignedJWT signedJWT = SignedJWT.parse(token);
            final JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

            final Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            final boolean verified = signedJWT.verify(jwsVerifier);

            if (!(verified && expiredTime.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            final boolean isExistsExpiredToken = expiredRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());
            if (isExistsExpiredToken) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            return signedJWT;
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
