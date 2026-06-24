package com.ecommerce.security.filter;

import com.ecommerce.security.token.JwtAuthenticationToken;
import com.ecommerce.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtValidationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtValidationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if(token != null){
                logger.info("🔍 JWT Token found in request");

                // Validate token first
                if(!jwtUtil.validateToken(token)){
                    logger.error("❌ JWT Token validation failed");
                    filterChain.doFilter(request, response);
                    return;
                }

                logger.info("✅ JWT Token validated");

                // Extract username and role from token
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                logger.info("   Username: {}", username);
                logger.info("   Role: {}", role);

                if(username == null || role == null){
                    logger.error("❌ Could not extract username or role from token");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Create JWT authentication token
                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);

                logger.info("📤 Authenticating with JwtAuthenticationProvider...");
                Authentication authResult = authenticationManager.authenticate(authenticationToken);

                if(authResult.isAuthenticated()){
                    logger.info("✅ Authentication successful");
                    logger.info("   Principal: {}", authResult.getPrincipal());
                    logger.info("   Authorities: {}", authResult.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    logger.info("✅ Authentication set in SecurityContext");
                } else {
                    logger.error("❌ Authentication failed");
                }
            }
        } catch (Exception e) {
            logger.error("❌ Filter error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}