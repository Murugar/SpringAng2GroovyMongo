package com.iqmsoft.mongo.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.iqmsoft.domain.Micropost;

public interface MicropostRepository extends MongoRepository<Micropost, String>, MicropostRepositoryCustom {
}
