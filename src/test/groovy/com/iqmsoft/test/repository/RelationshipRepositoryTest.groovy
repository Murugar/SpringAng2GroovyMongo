package com.iqmsoft.test.repository

import com.iqmsoft.mongo.repository.*
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired

class RelationshipRepositoryTest extends BaseRepositoryTest {

    @Autowired
    RelationshipRepository relationshipRepository

	@Autowired
	MicropostRepository micropostRepository
	
    @Autowired
    UserRepository userRepository
	
	Random randomno = new Random();

    def "can find by follower and followed"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser follower = new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz")
        MyUser followed = new MyUser(uid:Math.abs(randomno.nextInt()), username: "satoru@test.com", password: "secret", name: "satoru", fname:"zzzzzzzzzzzzzz")
        userRepository.save([follower, followed])
        Relationship relationship = new Relationship(follower: follower, followed: followed)

		relationshipRepository.save(relationship)
		
		followed.getFollowedRelations().add(relationship);
		follower.getFollowerRelations().add(relationship);
		userRepository.save([follower, followed])
		
        when:
        def result = relationshipRepository.findOneByFollowerAndFollowed(follower, followed)

        then:
	    result.isPresent()
		result.get().getFollower() != null  
		result.get().getFollowed() != null
      
    }
}
