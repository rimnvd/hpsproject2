package ru.itmo.userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.itmo.userservice.exceptions.UserAlreadyExistsException;
import ru.itmo.userservice.model.dto.JwtRequestDto;
import ru.itmo.userservice.model.dto.UserRegisterRequestDto;
import ru.itmo.userservice.services.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(
            description = "Log in",
            summary = "Log in to the system",
            tags = "Auth"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Incorrect username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody @Valid JwtRequestDto request) {
        try {
            return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные имя пользователя или пароль");
        }
    }

    @Operation(
            description = "Register",
            summary = "New user register",
            tags = "Auth"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User has successfully registered"),
            @ApiResponse(responseCode = "400", description = "User with this username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody @Valid UserRegisterRequestDto userRegisterRequest)  {
        try {
            return ResponseEntity.ok(authService.register(userRegisterRequest));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
