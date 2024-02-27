package ru.itmo.userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.exceptions.UserBlockedException;
import ru.itmo.userservice.services.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            description = "Delete user",
            summary = "Delete user by id if exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable @NotNull @Min(1) Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok("Пользователь с id " + userId + " успешно удален");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            description = "Set ADMIN role",
            summary = "Set admin role to user if exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @PostMapping("/set-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setAdminRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setAdminRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            description = "Remove ADMIN role",
            summary = "Remove ADMIN role if user exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @PostMapping("/remove-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeAdminRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.removeAdminRole(userId);
            return ResponseEntity.ok("Роль успешно удалена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(
            description = "Set PREMIUM USER role",
            summary = "Set PREMIUM USER role if user exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @PostMapping("/premium")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setPremiumUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setPremiumUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(
            description = "Set BLOCKED USER role",
            summary = "Set BLOCKED USER role if user exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @PostMapping("/blocked")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setBlockedUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setBlockedUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            description = "Set STANDARD USER role",
            summary = "Set STANDARD USER role if user exists",
            tags = "Admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "No rights")
    })
    @PostMapping("/standard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setStandardUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setStandardUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
