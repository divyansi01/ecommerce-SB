package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.model.UserDTO;
import com.ecommerce.model.UserRequest;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserService Tests")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Get User By Id")
    public void getUserById() {
        UserDTO user = userService.getUserById(1L);

        assertAll("userById",
                () -> assertNotNull(user),
                () -> assertEquals(user.getId(), 1L),
                () -> assertEquals(user.getUsername(), "admin"),
                () -> assertEquals(user.getRole(), "ADMIN"));
    }

    @Test
    @DisplayName("Get User By Id - Verify Email")
    public void getUserById_VerifyEmail() {
        UserDTO user = userService.getUserById(1L);

        assertAll("userEmail",
                () -> assertNotNull(user),
                () -> assertEquals(user.getEmail(), "admin@test.com"),
                () -> assertTrue(user.getEmail().contains("@")));
    }

    @Test
    @DisplayName("Get User By Invalid Id - Should Throw Exception")
    public void getUserById_InvalidId() {
        assertThrows(Exception.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    @DisplayName("Load User By Username - Admin")
    public void getUserByUsername_Admin() {
        var userDetails = userService.getUserByUsername("admin");

        assertAll("adminUserDetails",
                () -> assertNotNull(userDetails),
                () -> assertEquals(userDetails.getUsername(), "admin"),
                () -> assertTrue(userDetails.isEnabled()),
                () -> assertTrue(userDetails.isAccountNonExpired()),
                () -> assertTrue(userDetails.isAccountNonLocked()));
    }

    @Test
    @DisplayName("Load User By Username - Regular User")
    public void getUserByUsername_User() {
        var userDetails = userService.getUserByUsername("user1");

        assertAll("regularUserDetails",
                () -> assertNotNull(userDetails),
                () -> assertEquals(userDetails.getUsername(), "user1"),
                () -> assertTrue(userDetails.isEnabled()));
    }

    @Test
    @DisplayName("Load User By Username - Verify Authorities Admin")
    public void getUserByUsername_VerifyAuthorities_Admin() {
        var userDetails = userService.getUserByUsername("admin");

        assertAll("adminAuthorities",
                () -> assertNotNull(userDetails.getAuthorities()),
                () -> assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("Load User By Username - Verify Authorities User")
    public void getUserByUsername_VerifyAuthorities_User() {
        var userDetails = userService.getUserByUsername("user1");

        assertAll("userAuthorities",
                () -> assertNotNull(userDetails.getAuthorities()),
                () -> assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))));
    }

    @Test
    @DisplayName("Load User By Invalid Username - Should Throw Exception")
    public void getUserByUsername_InvalidUsername() {
        assertThrows(Exception.class, () -> {
            userService.getUserByUsername("nonexistent");
        });
    }

    @Test
    @DisplayName("Create User - Admin")
    public void createUser_Admin() {
        UserRequest request = UserRequest.builder()
                .username("newadmin")
                .email("newadmin@test.com")
                .password("password123")
                .role("ADMIN")
                .build();

        long initialCount = userRepository.count();

        UserDTO createdUser = userService.createUser(request);

        assertAll("createAdminUser",
                () -> assertNotNull(createdUser),
                () -> assertEquals("newadmin", createdUser.getUsername()),
                () -> assertEquals("newadmin@test.com", createdUser.getEmail()),
                () -> assertEquals("ADMIN", createdUser.getRole()),
                () -> assertEquals(userRepository.count(), initialCount + 1));
    }

    @Test
    @DisplayName("Create User - Regular User")
    public void createUser_RegularUser() {
        UserRequest request = UserRequest.builder()
                .username("newuser")
                .email("newuser@test.com")
                .password("password123")
                .role("USER")
                .build();

        long initialCount = userRepository.count();

        UserDTO createdUser = userService.createUser(request);

        assertAll("createRegularUser",
                () -> assertNotNull(createdUser),
                () -> assertEquals("newuser", createdUser.getUsername()),
                () -> assertEquals("USER", createdUser.getRole()),
                () -> assertEquals(userRepository.count(), initialCount + 1));
    }

    @Test
    @DisplayName("Create User - Default Role")
    public void createUser_DefaultRole() {
        UserRequest request = UserRequest.builder()
                .username("defaultuser")
                .email("default@test.com")
                .password("password123")
                .build();

        UserDTO createdUser = userService.createUser(request);

        assertAll("defaultRole",
                () -> assertNotNull(createdUser),
                () -> assertEquals("USER", createdUser.getRole()));
    }

    @Test
    @DisplayName("Create User - Verify in H2 Database")
    public void createUser_VerifyInDatabase() {
        UserRequest request = UserRequest.builder()
                .username("databaseuser")
                .email("databaseuser@test.com")
                .password("password123")
                .role("USER")
                .build();

        UserDTO createdUser = userService.createUser(request);

        UserDTO fetchedUser = userService.getUserById(createdUser.getId());
        assertAll("verifyInDatabase",
                () -> assertNotNull(fetchedUser),
                () -> assertEquals("databaseuser", fetchedUser.getUsername()),
                () -> assertEquals("databaseuser@test.com", fetchedUser.getEmail()));
    }

    @Test
    @DisplayName("Load User By Username - Verify Password")
    public void getUserByUsername_VerifyPassword() {
        var userDetails = userService.getUserByUsername("admin");

        assertAll("userPassword",
                () -> assertNotNull(userDetails.getPassword()),
                () -> assertFalse(userDetails.getPassword().isEmpty()));
    }

    @Test
    @DisplayName("Database has correct test data")
    public void verifyTestDataLoaded() {
        long totalUsers = userRepository.count();

        assertAll("testDataLoaded",
                () -> assertTrue(totalUsers >= 2),
                () -> assertDoesNotThrow(() -> userService.getUserById(1L)),
                () -> assertDoesNotThrow(() -> userService.getUserById(2L)));
    }
}