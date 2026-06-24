package com.ecommerce.config;

import com.ecommerce.security.filter.JwtAuthenticationFilter;
import com.ecommerce.security.filter.JwtValidationFilter;
import com.ecommerce.security.providers.JwtAuthenticationProvider;
import com.ecommerce.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigForJwt {
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfigForJwt(JwtUtil jwtUtil,
                                UserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(){
        return new JwtAuthenticationProvider(jwtUtil, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   JwtUtil jwtUtil) throws Exception{
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil);
        JwtValidationFilter jwtValidationFilter = new JwtValidationFilter(authenticationManager, jwtUtil);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/products/**").hasRole("ADMIN")
                .requestMatchers("/v1/performance/**").permitAll()
                .requestMatchers("/v1/cache/**").permitAll()
                .requestMatchers("/v1/users/**").permitAll()
                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtValidationFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider,
            JwtAuthenticationProvider jwtAuthenticationProvider) {

        return new ProviderManager(
                List.of(
                        daoAuthenticationProvider,
                        jwtAuthenticationProvider
                )
        );
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll())  // ✅ Allow all requests
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }
}
