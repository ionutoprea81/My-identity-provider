package com.id.provider.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.id.provider.config.JwtService;
import com.id.provider.models.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {



    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

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
        if(userEmail == null || userEmail.length() == 0){
            return ResponseEntity.badRequest().body("You should send refresh token!");
        }
        var user = repository.findByEmail(userEmail).orElseThrow();
        if(jwtService.isTokenValid(refreshToken,user)){
            var accessToken = jwtService.generateToken(user);
            var authResponse = AuthenticationResponse.builder().refreshToken(refreshToken).token(accessToken).build();
            return ResponseEntity.ok(authResponse);
        }

        return ResponseEntity.badRequest().body("The request is not valid");
    }
}
