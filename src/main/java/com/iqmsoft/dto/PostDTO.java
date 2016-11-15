package com.iqmsoft.dto;

import lombok.*;

import java.math.BigInteger;
import java.util.Date;

import com.iqmsoft.domain.Micropost;
import com.iqmsoft.domain.MyUser;

@Builder
@ToString(exclude = {"micropost", "user"})
@EqualsAndHashCode
public class PostDTO {

    private final Micropost micropost;
    private final MyUser user;
    private final UserStats userStats;

    @Getter
    @Setter
    private Boolean isMyPost = null;

    public String getId() {
        return micropost.getId();
    }
    
    public long getUid() {
        return micropost.getUid();
    }

    public String getContent() {
        return micropost.getContent();
    }
    
    public String getType() {
        return micropost.getType();
    }
    
    public String getTitle() {
        return micropost.getTitle();
    }

    public Date getCreatedAt() {
        return micropost.getCreatedAt();
    }

    public UserDTO getUser() {
        return UserDTO.builder()
                .user(user)
                .userStats(userStats)
                .isMyself(isMyPost)
                .build();
    }

}
