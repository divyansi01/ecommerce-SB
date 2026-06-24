package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.UserRequest;
import com.ecommerce.service.UserService;
import org.hibernate.query.QueryParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.RectangularShape;

@Controller
@RequestMapping(
        value = "/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService, final PasswordEncoder passwordEncoder){
        this.userService = userService;
    }

    @PostMapping(value = "/user/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<?> getUserById(@RequestParam Long id){
        return ResponseEntity.ok().body(userService.getUserById(id));
    }
}
