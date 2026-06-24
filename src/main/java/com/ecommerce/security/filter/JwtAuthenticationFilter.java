package com.ecommerce.security.filter;

import com.ecommerce.model.LoginRequest;
import com.ecommerce.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Only process login endpoint
        if(!request.getServletPath().equals("/generate-token")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            logger.info("Login attempt for user: {}", loginRequest.getUsername());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            Authentication authResult = authenticationManager.authenticate(authenticationToken);

            if(authResult.isAuthenticated()){
                logger.info("✅ User authenticated: {}", authResult.getName());

                // Get role from authenticated principal
                UserDetails userDetails = (UserDetails) authResult.getPrincipal();
                String role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(auth -> auth.replace("ROLE_", ""))  // Remove ROLE_ prefix
                        .findFirst()
                        .orElse("USER");

                logger.info("Role extracted: {}", role);

                // Generate access token
                String token = jwtUtil.generateToken(authResult.getName(), role);
                response.setHeader("Authorization", "Bearer " + token);

                logger.info("✅ Access token generated");

                // Generate refresh token
                String refreshToken = jwtUtil.generateToken(authResult.getName(), role);
                Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(true);
                refreshCookie.setPath("/refresh-token");
                refreshCookie.setMaxAge(60*60);
                response.addCookie(refreshCookie);

                logger.info("✅ Refresh token created");

                // Return success response
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Login successful\",\"token\":\"" + token + "\"}");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (AuthenticationException e) {
            logger.error("❌ Authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid credentials\"}");
        }
    }
}