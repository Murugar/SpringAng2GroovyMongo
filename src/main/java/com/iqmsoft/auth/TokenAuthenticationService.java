package com.iqmsoft.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.iqmsoft.domain.MyUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class TokenAuthenticationService {

    private static final String AUTH_HEADER_NAME = "x-auth-token";
    private static final String AUTH_USER_NAME = "x-user";
    
    private static final long YEAR = 1000 * 60 * 60 * 24 * 365;

    
    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    public void addAuthentication(HttpServletResponse response,
                           UserAuthentication authentication) {
        final MyUser user = (MyUser)authentication.getDetails();
       // System.out.println(user);
        
      //  user.setEnabled(true);
        //user.setExpires(System.currentTimeMillis() + YEAR);
        response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
        response.addHeader(AUTH_USER_NAME, user.getUsername());
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token == null || token.isEmpty()) return null;
        
        return tokenHandler
                .parseUserFromToken(token)
                .map(UserAuthentication::new)
                .orElse(null);
    }
}

