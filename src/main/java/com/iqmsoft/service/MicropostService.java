package com.iqmsoft.service;

import java.math.BigInteger;
import java.util.List;

import com.iqmsoft.domain.MyUser;
import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;

public interface MicropostService {

    void delete(String id);

    List<PostDTO> findAsFeed(PageParams pageParams);

    List<PostDTO> findByUser(MyUser user, PageParams pageParams);
}
