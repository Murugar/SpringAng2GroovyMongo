package com.iqmsoft.mongo.repository;

import java.util.List;

import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;

interface MicropostRepositoryCustom {

    List<PostDTO> findAsFeed(MyUser user, PageParams pageParams);

    List<Micropost> findByUser(MyUser user, PageParams pageParams);
}
