package com.id.provider.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.id.provider.config.JwtService;
import com.id.provider.models.User;
import com.id.provider.models.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*" )
@RestController
@RequestMapping("/api/v1")
public class IDController {

    @Autowired
    UserRepository userRepository;

    JwtService jwtService;

    @GetMapping("/fetch-role")
    public ResponseEntity<String> retrieveRole(@RequestHeader("Authorization") String token){
        this.jwtService = new JwtService();
        String data = jwtService.extractClaim(token.substring(7), Claims::getSubject);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try{
            jsonNode = objectMapper.readTree(data);
        }catch (Exception ex){
            return ResponseEntity.internalServerError().body("There was a problem while processing your request");
        }

        final String userEmail = jsonNode.get("email").asText();
        Optional<User> foundUser = userRepository.findByEmail(userEmail);
        if(foundUser.isPresent()){
            return ResponseEntity.ok(foundUser.get().getRole().toString());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String token){

        return ResponseEntity.ok("The token is valid");
    }
}
