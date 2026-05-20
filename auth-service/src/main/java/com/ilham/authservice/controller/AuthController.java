package com.ilham.authservice.controller;

import com.ilham.authservice.dao.request.LoginRequestDto;
import com.ilham.authservice.dao.request.RegisterRequestDto;
import com.ilham.authservice.dao.request.VerifyOtpRequestDto;
import com.ilham.authservice.dao.response.AuthResponseDto;
import com.ilham.authservice.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authenticationService;

    @PostMapping("/register")
    public String registerPatient(@RequestBody @Valid RegisterRequestDto request) {
        return authenticationService.register(request);
    }

    @PostMapping("/verify-otp")
    public AuthResponseDto verifyOtp(@RequestBody @Valid VerifyOtpRequestDto request) {
        return authenticationService.verifyOtp(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthResponseDto refreshToken(@RequestParam String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        return authenticationService.logout(authHeader);
    }
}
