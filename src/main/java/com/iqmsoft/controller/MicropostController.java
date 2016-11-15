package com.iqmsoft.controller;

import lombok.Data;

import java.math.BigInteger;

import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.exception.NotPermittedException;
import com.iqmsoft.mongo.repository.MicropostRepository;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.security.SecurityContextService;
import com.iqmsoft.service.MicropostService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/microposts")
public class MicropostController {

    private final MicropostRepository micropostRepository;
    
   
    private final UserRepository userRepository;
    
    private final MicropostService micropostService;
    private final SecurityContextService securityContextService;
    
    private static final Logger logger = LoggerFactory.getLogger(MicropostController.class);
    
    private Random r = new Random();
    
    

    @Autowired
    public MicropostController(MicropostRepository micropostRepository,
    		                   UserRepository userRepository,
                               MicropostService micropostService,
                               SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.userRepository = userRepository;
        this.micropostService = micropostService;
        this.securityContextService = securityContextService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Micropost create(@RequestBody MicropostParam param) {
        MyUser currentUser = securityContextService.currentUser();
        
        List<Micropost> l =  this.micropostRepository.findAll(new Sort(Sort.Direction.DESC,"uid"));
        
        Micropost p = null;
        
        AtomicInteger p1 = null;
        
        if ((l != null) && (l.size() != 0)) 
        {
           logger.debug("Not Null");	
           
           for(Micropost k : l)
           {
        	   p = k; break;
           }
           
           p1 = new AtomicInteger((int) p.getUid());
        }
        else
        {
        	logger.debug("Null");	
        	p1 = new AtomicInteger(0);
        }
        
        
        Micropost m = new Micropost(currentUser, Math.abs(p1.incrementAndGet()), param.getContent(), "plain", param.getTitle());
        
        m = micropostRepository.save(m);
        
        if(currentUser.getMicroposts() != null)
        {
        	 if(currentUser.getMicroposts().add(m))
        	 {
                this.userRepository.save(currentUser);
        	 }
        }
        
        return m;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") String id) {
       
    	Micropost m = micropostRepository.findOne(id);
    	
    	micropostService.delete(id);
        
        micropostRepository.delete(id);        
        
        MyUser u = securityContextService.currentUser();
        u.getMicroposts().remove(m);
        
        this.userRepository.save(u);
    }

    @Data
    private static class MicropostParam {
        private String content;
        private String type;
        private String title;
       // private long uid;
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotPermittedException.class)
    public void handleNoPermission() {
    	
    }
}
