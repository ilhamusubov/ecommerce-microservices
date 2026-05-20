package com.ilham.authservice.service.auth;

import com.ilham.authservice.dao.request.LoginRequestDto;
import com.ilham.authservice.dao.request.RegisterRequestDto;
import com.ilham.authservice.dao.request.VerifyOtpRequestDto;
import com.ilham.authservice.dao.response.AuthResponseDto;
import com.ilham.authservice.entity.RefreshTokenEntity;
import com.ilham.authservice.entity.UserEntity;
import com.ilham.authservice.enums.ErrorMessage;
import com.ilham.authservice.enums.RoleEnum;
import com.ilham.authservice.exceptions.CustomException;
import com.ilham.authservice.exceptions.ResourceNotFoundException;
import com.ilham.authservice.jwt.JwtService;
import com.ilham.authservice.jwt.TokenBlackListService;
import com.ilham.authservice.repository.RefreshTokenRepository;
import com.ilham.authservice.repository.UserRepository;
import com.ilham.authservice.service.mail.EmailService;
import com.ilham.authservice.service.mail.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;

    private final EmailService emailService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenBlackListService tokenBlacklistService;




    @Transactional
    public String register(RegisterRequestDto request){
        log.info("ActionLog.registerPatient.start");
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new CustomException(ErrorMessage.EMAIL_ALREADY_EXISTS);
        }

        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.USER)
                .enabled(false)
                .accountNonLocked(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(userEntity);

        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));
        otpService.saveOTP(request.getEmail(), otp);

        emailService.sendOtpEmail(request.getEmail(), otp);
        log.info("ActionLog.registerPatient.end");
        return "OTP sent to email";
    }


    @Transactional
    public AuthResponseDto verifyOtp(VerifyOtpRequestDto request) {

        log.info("ActionLog.verifyOtp.start");

        String savedOtp = otpService.getOTP(request.getEmail());

        if (savedOtp == null || savedOtp.isBlank()) {
            throw new CustomException(ErrorMessage.INVALID_OTP);
        }

        if (!savedOtp.equals(request.getOtp())) {
            throw new CustomException((ErrorMessage.INVALID_OTP));
        }

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        userEntity.setEnabled(true);
        userRepository.save(userEntity);

        String accessToken = jwtService.generateToken(userEntity);

        RefreshTokenEntity refreshToken = createRefreshToken(userEntity);

        otpService.deleteOTP(request.getEmail());

        log.info("ActionLog.verifyOtp.end");

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("Email verified successfully")
                .build();
    }




    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        log.info("ActionLog.login.start");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserEntity user = userRepository.findByEmail(request.getEmail()).
                orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!user.isEnabled()) {
            throw new CustomException(ErrorMessage.USER_NOT_VERIFIED);
        }

        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = jwtService.generateToken(user);

        RefreshTokenEntity refreshToken = createRefreshToken(user);

        String roleName = user.getRole().name().toLowerCase();

        String message =
                roleName.substring(0,1).toUpperCase()
                        + roleName.substring(1)
                        + " logged in successfully";

        log.info("ActionLog.login.end");

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message(message)
                .build();
    }




    @Transactional
    public AuthResponseDto refreshToken(String requestToken) {

        log.info("ActionLog.refreshToken.start");

        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CustomException(ErrorMessage.REFRESH_TOKEN_EXPIRED);
        }

        UserEntity user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new CustomException(ErrorMessage.USER_NOT_ACTIVE);
        }

        String newAccessToken = jwtService.generateToken(user);

        log.info("ActionLog.refreshToken.end");

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .message("Access token refreshed successfully")
                .build();
    }



    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshTokenEntity token = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(token);
    }



    @Transactional
    public String logout(String authHeader) {

        log.info("ActionLog.logout.start");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorMessage.INVALID_HEADER);
        }

        String token = authHeader.substring(7);

        long remainingTime = jwtService.getRemainingTime(token);

        if (remainingTime > 0) {
            tokenBlacklistService.blacklistToken(token, remainingTime);
        }

        UserEntity user = userRepository
                .findByEmail(jwtService.extractUsernameByToken(token))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        log.info("ActionLog.logout.end");

        return "Logged out successfully";
    }
}
