package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.model.UserDTO;
import com.ecommerce.model.UserRequest;

public interface UserService {
    UserDTO createUser(final UserRequest userRequest);
    User getUserById(final Long id);
}
