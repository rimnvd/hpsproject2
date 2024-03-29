package ru.itmo.userservice.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ru.itmo.userservice.services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            description = "Replenish the balance",
            summary = "Replenish user's balance",
            tags = "User"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User not found"),

    })
    @PostMapping("/replenish-balance")
    public ResponseEntity<?> replenishBalance(Principal principal, @RequestBody @NotNull @Min(1) Integer sum) {
        try {
            userService.replenishBalance(principal.getName(), sum);
            return ResponseEntity.ok("Баланас успешно пополнен");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            description = "Get all users",
            summary = "Get all user",
            tags = "User"
    )
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping
    public List<UserDto> getAllUsers(
            @PageableDefault(sort = {"id"}, size = 50) Pageable pageable
    ){
        return userService.getAll(pageable);
    }

    @Operation(
            description = "Buy premium account",
            summary = "Buy premium account",
            tags = "User"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "User doesn't have enough money")

    })
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

    @Hidden
    @GetMapping("/get-user-by-id")
    public ResponseDto<UserDto> getById(@RequestParam @NotNull @Min(1) Long id) {
        UserDto user = userService.getById(id);
        if (user == null) {
            return new ResponseDto<>(null, new NotFoundException(""),HttpStatus.NOT_FOUND);
        }
        return new ResponseDto<>(user, null, HttpStatus.OK);
    }

    @Hidden
    @GetMapping("/get-user-by-username")
    public ResponseDto<UserDto> findByUsername(@RequestParam @NotNull String username) {
        UserDto user = userService.getByUsername(username);
        if (user == null) {
            return new ResponseDto<>(null, new NotFoundException(""), HttpStatus.NOT_FOUND);
        }
        return new ResponseDto<>(user, null, HttpStatus.OK);
    }

    @Hidden
    @PostMapping("/update-balance")
    public ResponseEntity<String> updateBalance(@RequestBody @Valid UserUpdateBalanceDto userUpdateBalanceDto) {
        try {
            userService.updateBalance(userUpdateBalanceDto.getUserName(), userUpdateBalanceDto.getNewBalance());
            return ResponseEntity.ok("");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("");
        }
    }
}


