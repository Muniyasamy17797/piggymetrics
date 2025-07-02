package com.lms.user.controller;

import com.lms.user.dto.CreateUserDTO;
import com.lms.user.dto.UserDTO;
import com.lms.user.model.Role;
import com.lms.user.model.UserStatus;
import com.lms.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/status")
    public UserDTO updateUserStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        return userService.updateUserStatus(id, status);
    }

    @PostMapping("/{id}/roles")
    public UserDTO addRole(@PathVariable Long id, @RequestParam Role role) {
        return userService.addRole(id, role);
    }

    @DeleteMapping("/{id}/roles")
    public UserDTO removeRole(@PathVariable Long id, @RequestParam Role role) {
        return userService.removeRole(id, role);
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUsers(@RequestParam String query, Pageable pageable) {
        return userService.searchUsers(query, pageable);
    }

    @GetMapping("/by-date-range")
    public List<UserDTO> getUsersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return userService.getUsersByDateRange(startDate, endDate);
    }

    @GetMapping("/by-role")
    public List<UserDTO> getUsersByRole(@RequestParam Role role) {
        return userService.getUsersByRole(role);
    }

    @GetMapping("/by-status")
    public Page<UserDTO> getUsersByStatus(@RequestParam UserStatus status, Pageable pageable) {
        return userService.getUsersByStatus(status, pageable);
    }
}