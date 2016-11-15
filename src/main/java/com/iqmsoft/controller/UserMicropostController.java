package com.iqmsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;
import com.iqmsoft.mongo.repository.UserRepository;
import com.iqmsoft.security.SecurityContextService;
import com.iqmsoft.service.MicropostService;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserMicropostController {

    private final UserRepository userRepository;
    private final MicropostService micropostService;
    private final SecurityContextService securityContextService;

    @Autowired
    public UserMicropostController(UserRepository userRepository, MicropostService micropostService, SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.micropostService = micropostService;
        this.securityContextService = securityContextService;
    }

    @RequestMapping("/{userId:\\d+}/microposts")
    public List<PostDTO> list(@PathVariable("userId") Long userId, PageParams pageParams) {
       
        List<MyUser> l = userRepository.findAll();
    	
        
    	
    	
    	for(MyUser u : l)
    	{
    		if (u.getUid() == userId)
    		{
    			
    			 List<PostDTO> p = micropostService.findByUser(userRepository.findOne(u.getId()), pageParams);
    			
    			 if(p != null)
    			 {
    				 return p;
    			 }
    			
    		}
    	}
    	
    	return null;
        
    }

    @RequestMapping("/me/microposts")
    public List<PostDTO> list(PageParams pageParams) {
        final MyUser user = securityContextService.currentUser();
        return micropostService.findByUser(user, pageParams);
    }

}
