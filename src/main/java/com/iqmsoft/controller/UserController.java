package com.iqmsoft.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.iqmsoft.auth.TokenHandler;
import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.domain.Relationship;
import com.iqmsoft.dto.ErrorResponse;
import com.iqmsoft.dto.UserDTO;
import com.iqmsoft.dto.UserParams;
import com.iqmsoft.dto.UserStats;
import com.iqmsoft.mongo.repository.MicropostRepository;
import com.iqmsoft.mongo.repository.RelationshipRepository;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.security.SecurityContextService;
import com.iqmsoft.service.UserService;

import javax.annotation.Nullable;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Integer DEFAULT_PAGE_SIZE = 5;
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
   
    private final UserRepository userRepository;
    private final RelationshipRepository relRepository;
    private final MicropostRepository micRepository;
    private final UserService userService;
    private final SecurityContextService securityContextService;
    private final TokenHandler tokenHandler;

    @Autowired
    public UserController(UserRepository userRepository, RelationshipRepository relRepository, 
    		MicropostRepository micRepository, UserService userService, SecurityContextService securityContextService, TokenHandler tokenHandler) {
        this.userRepository = userRepository;
        this.relRepository = relRepository;
        this.micRepository = micRepository;
        this.userService = userService;
        this.securityContextService = securityContextService;
        this.tokenHandler = tokenHandler;
        logger.debug("User Controller Token" + this.tokenHandler);
    }

    @RequestMapping
    public Page<MyUser> list(@RequestParam(value = "page", required = false) @Nullable Integer page,
                           @RequestParam(value = "size", required = false) @Nullable Integer size) {
       
    	logger.debug("User Controller Token list " + this.tokenHandler);
    	
    	MyUser user = securityContextService.currentUser();
    	
	    List<MyUser> l = userRepository.findAll();
    	
    	for(MyUser u : l)
		{

			if ((user != null) && (u != null))
			{

				logger.debug("User " + u.getUid() + " " + user.getUid());

				if (u.getUid() == user.getUid()) {

					u.setMe(true);
					logger.debug("User Set " + u.getUid() + " " + user.getUid());
				} else {
					u.setMe(false);
				}
			}
		}
    	
    	userRepository.save(l);
    	
    	
    	final PageRequest pageable = new PageRequest(
                Optional.ofNullable(page).orElse(1) - 1,
                Optional.ofNullable(size).orElse(DEFAULT_PAGE_SIZE));
    	
        return userRepository.findAll(pageable);
        
       
        
    }

    @RequestMapping(method = RequestMethod.POST)
    public MyUser create(@Valid @RequestBody UserParams params) {
    	
    	
    	 List<MyUser> l =  this.userRepository.findAll(new Sort(Sort.Direction.DESC,"uid"));
         
         MyUser p = null;
         
         AtomicInteger p1 = null;
         
         if ((l != null) && (l.size() != 0)) 
         {
            logger.debug("My User Create Not Null");	
            
            for(MyUser k : l)
            {
         	   p = k; break;
            }
            
            
            p1 = new AtomicInteger((int) p.getUid());
           
            long p2 = p1.longValue();
            
            p2 = p2 + 1;
            
            params.setUid(p2);
         }
         else
         {
         	logger.debug("My User Create Null");	
         	p1 = new AtomicInteger(0);
         	params.setUid(1);
         }
         
    	
        
         
        logger.debug(params.NewUser().toString());
         
        return userRepository.save(params.NewUser());
    }

  
    
    @RequestMapping(value = "/remove/{id:\\d+}", method = RequestMethod.DELETE)
    public void remove(@PathVariable("id") Long id) {
    	
    //	logger.debug("Remove User " + id);
   // 	logger.warn("Remove User " + id);
    	
    	MyUser user = securityContextService.currentUser();
    	
    	List<MyUser> l = userRepository.findAll();
    	
    	Micropost p1 = null;
    	Relationship p2 = null;
    	Relationship p3 = null;
    	
    	MyUser u2 = null;
    	
    	MyUser u3 = null;
    	MyUser u4 = null;
    	
    	for(MyUser u : l)
    	{
    		if (u.getUid() == id)
    		{
    			Set<Relationship> a = u.getFollowedRelations();
    			Set<Relationship> b = u.getFollowerRelations();
    			
    			List<Micropost> c = u.getMicroposts();
    			
    			u2 = u;
    			
    			for(Micropost p : c)
    			{
    				p1 = p;
    				this.micRepository.delete(p.getId());
    			}
    			
    			for(Relationship p : a)
    			{
    				p2 = p;
    				
    				u3 = p.getFollowed();
    				
    				logger.debug("Remove User Followed " + u3);
    				this.relRepository.delete(p.getId());
    			}
    			
    			for(Relationship p : b)
    			{
    				p3 = p;
    				u4 = p.getFollower();
    				
    				logger.debug("Remove User Follower " + u4);
    				
    				this.relRepository.delete(p.getId());
    			}
    			
    		}
    	}
    	
    	
    	if(u3 != null)
    	{
    		u3.getFollowedRelations().remove(p3);
			userRepository.save(u3);
			
			logger.debug("Remove User Followed Relations " + u3);
    	}
    	
    	if(u4 != null)
    	{
    		u4.getFollowerRelations().remove(p2);
			userRepository.save(u4);
			
			logger.debug("Remove User Followed Relations " + u3);
    	}
    	
    	if(u2 != null)
    	{
    		if(p1 != null)
    		{
    		   u2.getMicroposts().remove(p1);
    		}
    		if(p2 != null)
    		{
    			u2.getFollowedRelations().remove(p2);
    			logger.debug("Delete Main User Followed" + u2);
    		}
    		if(p3 != null)
    		{
    			u2.getFollowerRelations().remove(p3);
    			logger.debug("Delete Main User Follower" + u2);
    		}
    		
    		logger.debug("Delete Main User " + u2);
    		
    	    userRepository.delete(u2.getId());
    	}
    	
    	
    	logger.debug("Remove User " + id);
    	
    }
        
    @RequestMapping(value = "{id:\\d+}", method = RequestMethod.GET)
    public UserDTO show(@PathVariable("id") Long id) {

    	logger.debug("Show User Init" + id);
    	
    	MyUser cuser = securityContextService.currentUser();
    	
    	List<MyUser> l = userRepository.findAll();
    	
    	
    	for(MyUser u : l)
		{

    		if(u != null)
			{
    			
				logger.debug("Show User " + u.getUid() + "  " + id);

				if (u.getUid() == id) {

					MyUser user = userRepository.findOne(u.getId());
					
					if(user != null)
					{
						UserStats ustats = new UserStats(user.getMicroposts().size(),
								user.getFollowedRelations().size(), user.getFollowerRelations().size(), false);

						logger.debug("Show User MyUser" + user.toString());

						UserDTO p =

								UserDTO.builder().user(user).userStats(ustats).isMyself(u.getUid() == cuser.getUid())
										.build();

						logger.debug("Show UserDTO" + p.toString());

						// userService.findOne(u.getId()).orElseThrow(UserNotFoundException::new);
						// logger.debug("Show User " + p.toString());
						return p;
					}
				}
			}
		}
    	logger.debug("Show User Null");
    	return null;
        
    }

    @RequestMapping("/me")
    public UserDTO showMe() {
    	
    	logger.debug("User Controller Token me " + this.tokenHandler);
    	
        return userService.findMe().orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "/me", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateMe(@Valid @RequestBody UserParams params) {
    	logger.debug("User Controller Token updateme " + this.tokenHandler);
    	
        MyUser user = securityContextService.currentUser();
        userService.update(user, params);

        // when username was changed, re-issue jwt.
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-auth-token", tokenHandler.createTokenForUser(user));
        headers.add("x-user", user.getUsername());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleValidationException(DataIntegrityViolationException e) {
        return new ErrorResponse("email_already_taken", "This email is already taken.");
    }

    @SuppressWarnings("serial")
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No user")
    private class UserNotFoundException extends RuntimeException {
    }
}
