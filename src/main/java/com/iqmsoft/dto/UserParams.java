package com.iqmsoft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iqmsoft.domain.MyUser;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Random;

@ToString
@EqualsAndHashCode
public final class UserParams {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserParams.class);

    private final String email;
    @Size(min = 6, max = 100)
    private final String password;
    private final String name;
    
    @Size(min = 10, max = 100)
    private final String fname;
    
    Random randomno = new Random();
    
    private long uid;

    public UserParams(@JsonProperty("email") String email,
                      @JsonProperty("password") String password,
                      @JsonProperty("name") String name,
                      @JsonProperty("fname") String fname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.fname = fname;
    }

    public Optional<String> getFName() {
        return Optional.ofNullable(fname);
    }
    
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getEncodedPassword() {
        return Optional.ofNullable(password).map(p -> new BCryptPasswordEncoder().encode(p));
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public MyUser toUser() {
        MyUser user = new MyUser();
        user.setUsername(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setName(name);
      //  user.setUid(Math.abs(randomno.nextLong()));
        user.setFname(fname);
        user.setEnabled(true);
        user.setAccountLocked(false);
        return user;
    }
    
    public void setUid(long uid)
    {
    	this.uid = uid;
    }
    
    public MyUser NewUser() {
        MyUser user = new MyUser();
        user.setUsername(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setName(name);
        logger.debug("User Params UID" + uid);
        user.setUid(uid);
        user.setFname(fname);
        user.setEnabled(true);
        user.setAccountLocked(false);
        return user;
    }

    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
