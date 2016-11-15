package com.iqmsoft.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqmsoft.dto.UserParams;
import com.iqmsoft.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger logger = LoggerFactory.getLogger(StatelessAuthenticationFilter.class);
	
	
    private final TokenAuthenticationService tokenAuthenticationService;
    private final UserService userService;

    public StatelessLoginFilter(String urlMapping,
                         TokenAuthenticationService tokenAuthenticationService,
                         UserService userService,
                         AuthenticationManager authenticationManager) {
        super(urlMapping);
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.userService = userService;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final UserParams params = new ObjectMapper().readValue(request.getInputStream(), UserParams.class);
        final UsernamePasswordAuthenticationToken loginToken = params.toAuthenticationToken();
        
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE. PATCH");
        response.addHeader("Access-Control-Allow-Credentials", "true");
       // response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        
        
        logger.debug("Attempt Auth " + getAuthenticationManager().toString());
      
        logger.debug("Attempt Auth " + loginToken.getPrincipal());
        
        logger.debug("Attempt Auth " + loginToken.isAuthenticated());
        
        logger.debug("Attempt Auth " + loginToken.getCredentials());
        
        return getAuthenticationManager().authenticate(loginToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        final UserDetails authenticatedUser = userService.loadUserByUsername(authResult.getName());
        final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

        tokenAuthenticationService.addAuthentication(response, userAuthentication);

        logger.debug("StatelessLoginFilter Success Auth " + userAuthentication.getPrincipal());
        
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
    }
}
