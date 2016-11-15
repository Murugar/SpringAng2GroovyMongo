package com.iqmsoft.mongo.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.RelatedUserDTO;
import com.iqmsoft.dto.UserDTO;

public interface UserRepositoryCustom {

    List<RelatedUserDTO> findFollowings(MyUser user, MyUser currentUser, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(MyUser user, MyUser currentUser, PageParams pageParams);

    Optional<UserDTO> findOne(String id, MyUser currentUser);
}
