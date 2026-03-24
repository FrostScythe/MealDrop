package com.restaurantmanagement.order_api.config;

import com.restaurantmanagement.order_api.security.JwtAuthFilter;
import com.restaurantmanagement.order_api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // enables @PreAuthorize on controllers
public class SecurityConfig {

    @Autowired private JwtAuthFilter jwtAuthFilter;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless REST APIs with JWT
                .csrf(csrf -> csrf.disable())

                // Define which endpoints are public vs protected
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — no token required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                        .requestMatchers("/images/**").permitAll()

                        // Role-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT,  "/api/restaurants/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/**").hasRole("OWNER")

                        .requestMatchers("/api/users/register/owner").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")

                        .requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Stateless — no HTTP session (each request must carry its JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Register our JWT filter BEFORE Spring's default login filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Use our custom UserDetailsService + password encoder
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    // DaoAuthenticationProvider wires UserDetailsService + PasswordEncoder together.
    // Spring calls this during login to verify credentials.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // BCrypt is the industry standard for password hashing.
    // It's slow by design (makes brute force attacks harder) and salts automatically.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager is needed by AuthController to trigger login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}