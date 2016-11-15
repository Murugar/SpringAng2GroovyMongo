package com.iqmsoft.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iqmsoft.controller.UserController;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.RelatedUserDTO;
import com.iqmsoft.dto.UserDTO;
import com.iqmsoft.dto.UserParams;
import com.iqmsoft.dto.UserStats;
import com.iqmsoft.mongo.repository.UserRepository;

import com.iqmsoft.security.SecurityContextService;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public MyUser update(MyUser user, UserParams params) {
        params.getEmail().ifPresent(user::setUsername);
        params.getEncodedPassword().ifPresent(user::setPassword);
        params.getName().ifPresent(user::setName);
        params.getFName().ifPresent(user::setFname);
        return userRepository.save(user);
    }

    @Override
    public List<RelatedUserDTO> findFollowings(MyUser user, PageParams pageParams) {
        final MyUser currentUser = securityContextService.currentUser();
        final List<RelatedUserDTO> followings = userRepository.findFollowings(user, currentUser, pageParams);
        followings.forEach(f -> {
            if (currentUser == null) return;
            f.setIsMyself(f.getUid() == currentUser.getUid());
        });
        return followings;
    }

    @Override
    public List<RelatedUserDTO> findFollowers(MyUser user, PageParams pageParams) {
        final MyUser currentUser = securityContextService.currentUser();
        final List<RelatedUserDTO> followers = userRepository.findFollowers(user, currentUser, pageParams);
        followers.forEach(f -> {
            if (currentUser == null) return;
            f.setIsMyself(f.getUid() == currentUser.getUid());
        });
        return followers;
    }

    @Override
    public Optional<UserDTO> findOne(String id) {
        final MyUser currentUser = securityContextService.currentUser();
        
       

        MyUser user = userRepository.findOne(id);
        
        UserStats u = new UserStats(user.getMicroposts().size(), user.getFollowedRelations().size(),
				user.getFollowerRelations().size(), false);
        
        
        if (currentUser != null)
        {
		  return Optional.ofNullable(UserDTO.builder().user(user)
				.userStats(u).isMyself(user.getUid() == currentUser.getUid()).
				build());
        }
        else
        {
        	return Optional.ofNullable(UserDTO.builder().user(user)
    				.userStats(u).
    				build());
        	
        }
        /*final Optional<UserDTO> user = userRepository.findOne(id, currentUser);
        user.ifPresent(u -> {
            if (currentUser == null) return;
            u.setIsMyself(u.getId() == currentUser.getId());
        });*/
        
       // MyUser u1 = userRepository.findOne(id);
        
       // UserStats u = new UserStats(currentUser.getMicroposts().size(), currentUser.getFollowedRelations().size(),
				//currentUser.getFollowerRelations().size(), false);
        
        //return Optional.ofNullable(UserDTO.builder().user(currentUser).userStats(u).build());
        
        
    }

    @Override
    public Optional<UserDTO> findMe() {
        final MyUser currentUser = securityContextService.currentUser();
        return findOne(currentUser.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<MyUser> user = userRepository.findOneByUsername(username);
        
        logger.debug("loadUserByName " + user.get().getName());
        
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.get().setEnabled(true);
        
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }
}
