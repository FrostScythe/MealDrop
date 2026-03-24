package com.restaurantmanagement.order_api.security;

import com.restaurantmanagement.order_api.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter guarantees this runs exactly once per request

    @Autowired private JwtUtils jwtUtils;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Read the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. If no token or wrong format, skip this filter entirely
        //    (Spring Security will then block the request if the endpoint requires auth)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // pass to next filter
            return;
        }

        // 3. Extract the token (strip "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extract email from token
            final String email = jwtUtils.extractEmail(jwt);

            // 5. Only proceed if we got an email AND no auth is already set
            //    (SecurityContextHolder stores auth for the current thread)
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load full user from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7. Validate the token
                if (jwtUtils.isTokenValid(jwt, userDetails)) {

                    // 8. Create an Authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                          // credentials (not needed post-auth)
                                    userDetails.getAuthorities()); // roles

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // 9. Store in SecurityContext — this is how Spring Security
                    //    "knows" who the current user is for the rest of the request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token — just don't set authentication
            // The request will be rejected by Spring Security if auth is required
        }

        // 10. Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}