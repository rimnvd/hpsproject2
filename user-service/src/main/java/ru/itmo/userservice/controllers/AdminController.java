package ru.itmo.userservice.controllers;

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
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable @NotNull @Min(1) Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok("Пользователь с id " + userId + " успешно удален");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }

    @PostMapping("/set-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setAdminRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setAdminRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }

    @PostMapping("/remove-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeAdminRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.removeAdminRole(userId);
            return ResponseEntity.ok("Роль успешно удалена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }

    @PostMapping("/premium")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setPremiumUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setPremiumUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException | UserBlockedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }

    @PostMapping("/blocked")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setBlockedUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setBlockedUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }

    @PostMapping("/standard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> setStandardUserRole(@RequestBody @NotNull @Min(1) Long userId) {
        try {
            adminService.setStandardUserRole(userId);
            return ResponseEntity.ok("Роль успешно назначена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());        }
    }
}
