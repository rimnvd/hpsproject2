package ru.itmo.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.exceptions.UserAlreadyExistsException;
import ru.itmo.userservice.model.dto.JwtResponseDto;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.dto.UserRegisterRequestDto;
import ru.itmo.userservice.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto login(String username, String password) throws BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }

    public UserDto register(UserRegisterRequestDto userRegisterRequest) throws UserAlreadyExistsException {
        return userService.register(userRegisterRequest);
    }





}
