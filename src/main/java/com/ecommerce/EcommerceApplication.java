package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@EnableMethodSecurity(prePostEnabled = true)
public class EcommerceApplication {
    public static void main(String[] args){
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
