package com.iqmsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iqmsoft.exception.*;
import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;
import com.iqmsoft.exception.NotPermittedException;
import com.iqmsoft.mongo.repository.MicropostRepository;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.security.SecurityContextService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
public class MicropostServiceImpl implements MicropostService {

    private final MicropostRepository micropostRepository;
    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostServiceImpl(MicropostRepository micropostRepository, UserRepository userRepository,
    		SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.userRepository = userRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public void delete(String id) {
        
    	MyUser shit = securityContextService.currentUser();
    	
        Optional<MyUser> ns = Optional.ofNullable(shit); 
    	
        Micropost micropost = micropostRepository.findOne(id);
        
        
        if(ns != null)
		{
			if (ns.isPresent()) {
				if (ns.get() != micropost.getUser())
				{
					//throw new NotPermittedException("no permission to delete this post");
				}
					
			} else {
				throw new NotPermittedException("no permission to delete this post");
			}
		}
        else
        {
        	throw new NotPermittedException("no permission to delete this post");
        }
        
        Micropost m = micropostRepository.findOne(id);
        
        micropostRepository.delete(id);        
        
        securityContextService.currentUser().getMicroposts().remove(m);
        
       
    }

    @Override
    public List<PostDTO> findAsFeed(PageParams pageParams) {
        final MyUser currentUser = securityContextService.currentUser();
        final List<PostDTO> feed = micropostRepository.findAsFeed(currentUser, pageParams);
        feed.forEach(p -> p.setIsMyPost(p.getUser().getId() == currentUser.getId()));
        return feed;
    }

    @Override
    public List<PostDTO> findByUser(MyUser user, PageParams pageParams) {
        final MyUser currentUser = securityContextService.currentUser();
       
        final Boolean isMyPost = (currentUser != null) ? (currentUser.getId().equals(user.getId())) : null;
        
        
        List<PostDTO> p = new ArrayList<>();
        
        List<Micropost> q = micropostRepository.findByUser(user, pageParams);
       
        if(q == null)
        {
        	return p;
        }
        		
        for(Micropost m : q)
        {
        	if (m != null)
        	{
        	  p.add(PostDTO.builder()
             .micropost(m)
             .user(m.getUser())
             .isMyPost(isMyPost)
             .build());
           }
        		
        }
        
        return p;
    }

}
