package com.iqmsoft.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.*;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    MyUser update(MyUser user, UserParams params);

    List<RelatedUserDTO> findFollowings(MyUser user, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(MyUser user, PageParams pageParams);

    Optional<UserDTO> findOne(String id);

    Optional<UserDTO> findMe();
}
