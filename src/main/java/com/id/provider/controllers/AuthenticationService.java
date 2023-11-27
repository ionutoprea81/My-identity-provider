package com.id.provider.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.id.provider.config.JwtService;
import com.id.provider.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public String register(RegisterRequest request) {

        var user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
        var savedUser = repository.save(user);
        return "";
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
    }

    public ResponseEntity<?> refreshToken(String refreshToken) throws JsonProcessingException {
        final String decodedJwt = jwtService.extractUsername(refreshToken);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(decodedJwt);
        final String userEmail = jsonNode.get("email").asText();
        if (userEmail == null || userEmail.length() == 0) {
            return ResponseEntity.badRequest().body("You should send refresh token!");
        }
        var user = repository.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshToken, user)) {
            var accessToken = jwtService.generateToken(user);
            var authResponse = AuthenticationResponse.builder().refreshToken(refreshToken).token(accessToken).build();
            return ResponseEntity.ok(authResponse);
        }

        return ResponseEntity.badRequest().body("The request is not valid");
    }

    public void createPasswordResetTokenForUser(String email, String token) {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime expiry = current.plusHours(2);

        PasswordResetToken myToken = new PasswordResetToken(token, email, Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()));
        passwordResetTokenRepository.save(myToken);
    }

    public void resetPasswordForUser(String token, String password) throws Exception {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (!passwordResetToken.isPresent()) {
            throw new Exception("Invalid reset code.");
        }

        LocalDateTime current = LocalDateTime.now();

        if (passwordResetToken.get().getExpDate().toInstant().isBefore(current.atZone(ZoneId.systemDefault()).toInstant())) {
            throw new Exception("Expired reset code.");
        }

        repository.changeUserPassword(passwordResetToken.get().getEmail(), passwordEncoder.encode(password));
    }

}
