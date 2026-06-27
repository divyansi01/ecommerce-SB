package com.ecommerce.service.impl;


import com.ecommerce.model.User;
import com.ecommerce.model.UserDTO;
import com.ecommerce.model.UserRequest;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(final UserRequest userRequest){
        logger.info("Creating New User: {}", userRequest.getUsername());
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole() != null ? userRequest.getRole() : "USER")
                .build();
        return convertToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(final Long id, final UserRequest request) {
        logger.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (!isAdminOrOwner(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have permission to update this user");
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            logger.info("Password updated for user: {}", user.getUsername());
        }

        if (request.getRole() != null && isAdmin()) {
            user.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", id);

        return convertToDTO(updatedUser);
    }

    @Override
    public User getUserById(final Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User does not exists with id:",id)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("User does not exists with username:", username)));
    }

    @Override
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails getLoggedInUser(){
        logger.info("Fetching Logged In User");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(Objects.isNull(auth) || !auth.isAuthenticated()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Authenticated");
        }
        return loadUserByUsername(auth.getName());
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        logger.info("Changing password for user id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (!isAdminOrOwner(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have permission to change this password");
        }

        if (!isAdmin()) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
            }
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed successfully for user: {}", user.getUsername());
    }

    private boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isAdminOrOwner(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = auth.getName().equals(username);

        return isAdmin || isOwner;
    }

    private UserDTO convertToDTO(final User user){
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
