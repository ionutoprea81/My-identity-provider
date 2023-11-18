package com.id.provider.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.id.provider.models.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest){
        String origin = httpServletRequest.getHeader(HttpHeaders.ORIGIN);
        System.out.println(origin);
        return ResponseEntity.ok(service.authenticate(request));
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
