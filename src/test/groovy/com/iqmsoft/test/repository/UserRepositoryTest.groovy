package com.iqmsoft.test.repository

import com.iqmsoft.mongo.repository.*
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.dto.PageParams
import com.iqmsoft.dto.RelatedUserDTO
import com.iqmsoft.dto.UserDTO

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UserRepository userRepository
	
	@Autowired
	MicropostRepository micropostRepository

    @Autowired
    RelationshipRepository relationshipRepository
	
	Random randomno = new Random();

    def "findFollowers"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser currentUser = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "current@test.com", password: "secret", name: "current", fname:"zzzzzzzzzzzzzz"))

        MyUser u1 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test1@test.com", password: "secret", name: "test1", fname:"zzzzzzzzzzzzzz"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: user, followed: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))
		
		currentUser.getFollowerRelations().add(r1);
	
		u1.getFollowedRelations().add(r1);
		
		userRepository.save(currentUser)
		userRepository.save(u1)
		

        MyUser u2 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test2@test.com", password: "secret", name: "test2", fname:"zzzzzzzzzzzzzz"))
        Relationship r2 = relationshipRepository.save(new Relationship(follower: user, followed: u2))

		
		u2.getFollowedRelations().add(r2);
		user.getFollowerRelations().add(r2);
		
		userRepository.save(user)
		
		userRepository.save(u2)
		
        when:
        List<RelatedUserDTO> result = userRepository.findFollowers(user, currentUser, new PageParams())

        then:
		result != null
		result.size() == 1
        result[0].email == "test2@test.com"
        //!result[0].userStats.isFollowedByMe()
      //  result[0].relationshipId == r2.id
        //result[1].email == "akira@test.com"
        //result[1].userStats.isFollowedByMe()
       // result[1].relationshipId == r1.id
    }

    def "findFollowings"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser currentUser = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "current@test.com", password: "secret", name: "current", fname:"zzzzzzzzzzzzzz"))

        MyUser u1 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test1@test.com", password: "secret", name: "test1", fname:"zzzzzzzzzzzzzz"))
        Relationship r1 = relationshipRepository.save(new Relationship(followed: user, follower: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))

		
		currentUser.getFollowerRelations().add(r1);
		u1.getFollowedRelations().add(r1);
		
		userRepository.save(currentUser)
		userRepository.save(u1)
		
		
        MyUser u2 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test2@test.com", password: "secret", name: "test2", fname:"zzzzzzzzzzzzzz"))
        Relationship r2 = relationshipRepository.save(new Relationship(followed: user, follower: u2))

		u1.getFollowerRelations().add(r2);
		user.getFollowedRelations().add(r2);
		
		userRepository.save(user)
		userRepository.save(u1)
		
        when:
        List<RelatedUserDTO> result = userRepository.findFollowings(user, currentUser, new PageParams())

        then:
		result != null
		result.size() == 1
        result[0].email == "test2@test.com"
       // !result[0].userStats.isFollowedByMe()
       // result[0].relationshipId == r2.id
     //   result[1].email == "akira@test.com"
       // result[1].userStats.isFollowedByMe()
      //  result[1].relationshipId == r1.id
    }

    def "findOne"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser currentUser = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "current@test.com", password: "secret", name: "current", fname:"zzzzzzzzzzzzzz"))

        when:
        UserDTO result = userRepository.findOne(user.id, currentUser).get();

        then:
        result.id == currentUser.id
        !result.userStats.followedByMe
    }
	
	def "deleteUser"() {
		
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
		
		MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
		
		when:
		userRepository.delete(user.id)
		
		then:
		userRepository.count()  == 0
	}

}
