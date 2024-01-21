package ru.itmo.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.userservice.exceptions.UserAlreadyExistsException;
import ru.itmo.userservice.model.dto.JwtRequestDto;
import ru.itmo.userservice.model.dto.UserRegisterRequestDto;
import ru.itmo.userservice.services.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody @Valid JwtRequestDto request) {
        try {
            return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные имя пользователя или пароль");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody @Valid UserRegisterRequestDto userRegisterRequest)  {
        try {
            return ResponseEntity.ok(authService.register(userRegisterRequest));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
