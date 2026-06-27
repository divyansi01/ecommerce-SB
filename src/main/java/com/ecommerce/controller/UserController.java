package com.ecommerce.controller;

import com.ecommerce.model.ApiResponse;
import com.ecommerce.model.User;
import com.ecommerce.model.UserRequest;
import com.ecommerce.model.UserDTO;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(final UserService userService){
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getUserById(id), "User retrieved successfully")
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user")
    public ResponseEntity<ApiResponse<UserDetails>> getCurrentUser() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getLoggedInUser(), "Current user retrieved successfully")
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new user")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody UserRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User updated successfully")
        );
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("New password must be at least 6 characters"));
        }

        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Password changed successfully")
        );
    }
}