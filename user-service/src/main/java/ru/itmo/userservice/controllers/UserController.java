package ru.itmo.userservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.userservice.exceptions.NotEnoughMoneyException;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.ResponseDto;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.dto.UserUpdateBalanceDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    @PostMapping("/replenish-balance")
    public ResponseEntity<?> replenishBalance(Principal principal, @RequestBody @NotNull @Min(1) Integer sum) {
        try {
            userService.replenishBalance(principal.getName(), sum);
            return ResponseEntity.ok("Баланас успешно пополнен");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public List<UserDto> getAllUsers(
            @PageableDefault(sort = {"id"}, size = 50) Pageable pageable
    ){
        return userService.getAll(pageable);
    }

    @PostMapping("/buy-premium")
    public ResponseEntity<?> buyPremiumAccount(Principal principal) {
        try {
            userService.buyPremiumAccount(principal.getName());
            return ResponseEntity.ok("Покупка успешно совершена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotEnoughMoneyException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/get-user")
    public ResponseDto<UserEntity> getById(@RequestParam @NotNull @Min(1) Long id) {
        UserEntity user = userService.getById(id);
        if (user == null) {
            return new ResponseDto<>(null, new NotFoundException(""),HttpStatus.NOT_FOUND);
        }
        return new ResponseDto<>(user, null, HttpStatus.OK);
    }
    @GetMapping("/find")
    public ResponseDto<UserEntity> findByUsername(@RequestParam @NotNull String username) {
        Optional<UserEntity> user = userService.findByUsername(username);
        return user
                .map(userEntity -> new ResponseDto<>(userEntity, null, HttpStatus.OK))
                .orElseGet(() -> new ResponseDto<>(null, new NotFoundException(""), HttpStatus.NOT_FOUND));
    }

    @PostMapping("/update-balance")
    public ResponseEntity<?> updateBalance(@RequestBody @NotNull @Valid UserUpdateBalanceDto userUpdateBalanceDto) {
        try {
            userService.updateBalance(userUpdateBalanceDto.getUserId(), userUpdateBalanceDto.getNewBalance());
            return ResponseEntity.ok("");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("");
        }
    }
}


