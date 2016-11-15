package com.iqmsoft.mongo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.iqmsoft.domain.MyUser;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<MyUser, String>, UserRepositoryCustom {

    Optional<MyUser> findOneByUsername(String username);
    

}
