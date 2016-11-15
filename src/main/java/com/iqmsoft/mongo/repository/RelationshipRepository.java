package com.iqmsoft.mongo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.iqmsoft.domain.Relationship;
import com.iqmsoft.domain.MyUser;

import java.math.BigInteger;
import java.util.Optional;

public interface RelationshipRepository extends MongoRepository<Relationship, String> {
    Optional<Relationship> findOneByFollowerAndFollowed(MyUser follower, MyUser followed);
}
