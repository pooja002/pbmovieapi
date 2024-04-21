package com.pbmovies.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class AuthFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public AuthFilterService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

         String jwt,username;

        if(authHeader==null || authHeader.startsWith("Bearer "))
        {
             filterChain.doFilter(request,response);
             return;
        }

        // extract jwt
        jwt = authHeader.substring(7);

        // extract username from jwt
        username = jwtService.extractUsername(jwt);

        // check if user is not authenticated yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
          UserDetails userDetails =   userDetailsService.loadUserByUsername(username);
          if(jwtService.isTokenValid(jwt, userDetails))
          {

          }
        }

    }
}
