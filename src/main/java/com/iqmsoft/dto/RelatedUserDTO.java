package com.iqmsoft.dto;

import com.iqmsoft.domain.Relationship;

import java.math.BigInteger;

import com.iqmsoft.domain.MyUser;

import lombok.*;

@Builder
@ToString(exclude = {"user", "relationship"})
@EqualsAndHashCode
public class RelatedUserDTO {

    private final MyUser user;

    @Getter
    private final UserStats userStats;

    private final Relationship relationship;

    @Getter
    @Setter
    private Boolean isMyself = null;

    public String getId() {
        return user.getId();
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

    public String getRelationshipId() {
        return relationship.getId();
    }

}
