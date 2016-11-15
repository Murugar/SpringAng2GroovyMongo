package com.iqmsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.RelatedUserDTO;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.service.UserService;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
public class UserRelationshipController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserRelationshipController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping("/followings")
    public List<RelatedUserDTO> followings(@PathVariable("userId") long userId, PageParams pageParams) {
        
        List<MyUser> l = userRepository.findAll();
    	
    	MyUser u1 = null;
    	
    	for(MyUser u : l)
    	{
    		if (u.getUid() == userId)
    		{
    			final MyUser user = userRepository.findOne(u.getId());
    	        return userService.findFollowings(user, pageParams);
    		}
    	}
    	
    	return null;
    	
    }

    @RequestMapping("/followers")
    public List<RelatedUserDTO> followers(@PathVariable("userId") long userId, PageParams pageParams) {
       
    	List<MyUser> l = userRepository.findAll();
      	
      	MyUser u1 = null;
      	
      	for(MyUser u : l)
      	{
      		if (u.getUid() == userId)
      		{
      			final MyUser user = userRepository.findOne(u.getId());
      	        return userService.findFollowers(user, pageParams);
      		}
      	}
    	
    	return null;
    	
    }
}
