package com.iqmsoft.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;

//import org.springframework.data.annotation.Id;

@Document
//@Entity
//@Table(name = "relationship", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
@NoArgsConstructor
@Data
public class Relationship implements Serializable {

    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    private String id;

    @DBRef(lazy=true)
    @NotNull
    
    @Getter
    @Setter
   // @ManyToOne
    @JsonIgnore
    private MyUser follower;

    @DBRef(lazy=true)
    @NotNull
 //   @ManyToOne
    @JsonIgnore
    @Getter
    @Setter
    private MyUser followed;

    public Relationship(MyUser follower, MyUser followed) {
        this.follower = follower;
        this.followed = followed;
    }
}
