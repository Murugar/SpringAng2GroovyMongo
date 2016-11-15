package com.iqmsoft.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.iqmsoft.auth.StatelessAuthenticationFilter;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.mongo.repository.UserRepository;

import java.util.Optional;

@Service
public class SecurityContextServiceImpl implements SecurityContextService {

    private final UserRepository userRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityContextServiceImpl.class);

    @Autowired
    public SecurityContextServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
       
  
  
    
    
    @Override
    public MyUser currentUser() {
        try {
         	Optional<MyUser> currentUser = userRepository.findOneByUsername
					(SecurityContextHolder.getContext().getAuthentication().getName());
         	
         	logger.debug("Security Impl " + currentUser.get().getName());
         	
			return currentUser.orElse(null);
		} catch (Exception e) {
			return null; 
		}
    }
}
