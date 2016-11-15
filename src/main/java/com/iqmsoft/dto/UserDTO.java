package com.iqmsoft.dto;

import java.math.BigInteger;

import com.iqmsoft.domain.MyUser;

import lombok.*;

@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode
public class UserDTO {

    private final MyUser user;

    @Getter
    private final UserStats userStats;

    @Getter
    @Setter
    private Boolean isMyself = null;

    public String getId() {
        return user.getId();
    }

    public boolean getMe() {
        return user.isMe();
    }
    
    public long getUid() {
        return user.getUid();
    }
    
    public String getEmail() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }

    public String getfname() {
        return user.getFname();
    }

}
