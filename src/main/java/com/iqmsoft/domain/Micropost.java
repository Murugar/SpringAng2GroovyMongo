package com.iqmsoft.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

//import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

@Document
//@Entity
//@Table(name = "micropost")
@Data
@NoArgsConstructor
public class Micropost {

    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    private String id;

    @DBRef(lazy = true)
    @NotNull
   // @ManyToOne//(fetch = FetchType.EAGER)
    private MyUser user;

    @NotNull
    private String content;
    
    @NotNull
    private String title;
    
    @NotNull
    private long uid;
    
    private String type;

    @NotNull
    @Column(name = "created_at")
    private Date createdAt;

    public Micropost(MyUser user, String content) {
        this.user = user;
        this.content = content;
    }
    
    public Micropost(MyUser user, String content, String type) {
        this.user = user;
        this.content = content;
        this.type = type;
    }
    
    public Micropost(MyUser user, String content, String type, String title) {
        this.user = user;
        this.content = content;
        this.type = type;
        this.title = title;
    }
    
    public Micropost(MyUser user, long uid, String content, String type, String title) {
        this.user = user;
        this.content = content;
        this.type = type;
        this.title = title;
        this.uid = uid;
        this.createdAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

}
