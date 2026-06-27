package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.model.UserDTO;
import com.ecommerce.model.UserRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    UserDTO createUser(final UserRequest userRequest);

    UserDTO updateUser(final Long id, final UserRequest request);

    User getUserById(final Long id);

    List<UserDTO> getAllUsers();

    UserDetails getLoggedInUser();

    User getUserByUsername(String username);

    void changePassword(Long id, String oldPassword, String newPassword);
}
