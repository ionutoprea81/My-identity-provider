package com.id.provider.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.id.provider.config.MailService;
import com.id.provider.models.PasswordResetToken;
import com.id.provider.models.PasswordResetTokenRepository;
import com.id.provider.models.User;
import com.id.provider.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    AuthenticationService service;
    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){

        Optional<User> foundUser = userRepository.findByEmail(request.getEmail());
        if(foundUser.isPresent()){
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("The user already exists in the database!").build());
        }

        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest){
        String origin = httpServletRequest.getHeader(HttpHeaders.ORIGIN);
        System.out.println(origin);
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/send-reset-password-token")
    public ResponseEntity<?> sendResetToken(@RequestBody ResetRequest request){
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(user == null){
            return ResponseEntity.badRequest().body("User details must not be null");
        }

        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            service.createPasswordResetTokenForUser(request.getEmail(), token);
            mailService.sendMail(request.getEmail(), "Reset password code:", token);
        }

        return ResponseEntity.ok().body("The code has been sent.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordReset passwordReset) {
        try {
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(passwordReset.getToken());

            if (passwordResetToken.isPresent()) {
                service.resetPasswordForUser(passwordReset.getToken(), passwordReset.getNewPass());
                return ResponseEntity.ok().body("Password updated.");
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return  ResponseEntity.internalServerError().body("");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshBody){

        if(refreshBody.getRefreshToken()!=null && refreshBody.getRefreshToken().length()!=0){
            try{
                return ResponseEntity.ok(service.refreshToken(refreshBody.getRefreshToken()));
            } catch (JsonProcessingException ex){
                System.out.println(ex.getMessage());
                return  ResponseEntity.badRequest().body("There was a problem with the request");
            }
        }
        return ResponseEntity.badRequest().body("You must provide refresh token in body");
    }
}
