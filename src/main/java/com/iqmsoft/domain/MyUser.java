package com.iqmsoft.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Type;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.springframework.data.annotation.Id;

//@Embeddable
//@Entity
@Document
//@Table(name = "myuser", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@ToString
public class MyUser implements UserDetails {

    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private String id;

    
    @NotNull
    @Getter
    @Setter
    private long uid;
    
    @NotNull
    @Getter
    @Setter
    private boolean me = false;
    
    
    @NotNull
    @Size(min = 4, max = 30)
    @Setter
    @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Size(min = 4, max = 30)
    @Getter
    @Setter
    private String name;
    
    @Transient
	private long expires;
    
    @Column(columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
  	private boolean enabled = false;
  	
    @Column(columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
  	private boolean locked = false;
    
    @NotNull
    @Size(min = 10, max = 60)
    @Getter
    @Setter
    @Pattern(regexp = "^[a-zA-Z ]*$")
    private String fname;

    @DBRef (lazy=true)
   // @OneToMany//(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @Getter
    @Setter
    private List<Micropost> microposts = new ArrayList<>();

    @DBRef (lazy=true)
   // @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @Getter
    @Setter
    private Set<Relationship> followerRelations = new HashSet<Relationship>();

    @DBRef (lazy=true)
  //  @OneToMany(mappedBy = "followed", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    @Getter
    @Setter
    private Set<Relationship> followedRelations =  new HashSet<Relationship>();

    @Override
    @JsonProperty("email")
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
        
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}
	
	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
	public boolean getAccountLocked() {
		return this.locked;
	}

	public void setAccountLocked(boolean locked) {
		this.locked = locked;
	}
	
}
