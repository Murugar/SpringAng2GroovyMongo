package com.iqmsoft.test.service

import com.iqmsoft.service.*
import com.iqmsoft.security.*
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.dto.PageParams
import com.iqmsoft.dto.UserDTO
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.mongo.repository.MicropostRepository

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired

@SuppressWarnings("GroovyPointlessBoolean")
class UserServiceTest extends BaseServiceTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository
	
	@Autowired
	MicropostRepository micropostRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    UserService userService
	
	Random randomno = new Random()

    def setup() {
        userService = new UserServiceImpl(userRepository, securityContextService)
    }

    def "can find followings when not signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser follower = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "follower@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser followed = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "followed@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        
		Relationship r1 = new Relationship(follower: follower, followed: followed)
		relationshipRepository.save(r1)

		follower.getFollowerRelations().add(r1);
		followed.getFollowedRelations().add(r1);
		
		userRepository.save(follower)
		userRepository.save(followed)
		
        when:
	    def followings = userService.findFollowings(follower, new PageParams())

        then:
	    followings != null
		followings.size() == 0
    }

   def "can find followings when signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		
        given:
        MyUser follower = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "follower@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser followed1 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "followed1@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser followed2 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "followed2@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        Relationship r2 = relationshipRepository.save(new Relationship(follower: follower, followed: followed2))
        securityContextService.currentUser() >> followed1

		follower.getFollowerRelations().add(r1);
		follower.getFollowerRelations().add(r2);
		followed1.getFollowedRelations().add(r1);
		followed1.getFollowedRelations().add(r2);
		
		userRepository.save(follower)
		userRepository.save(followed1)
		userRepository.save(followed2)
		
        when:
		1+1
        def followings = userService.findFollowings(followed1, new PageParams())

        then:
		true
        followings.first().email == "follower@test.com"
        //followings.first().isMyself == false
        //followings.last().isMyself == true
    }

   def "can find followers when not signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser followed = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "followed@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser follower = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "follower@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: follower, followed: followed))

		followed.getFollowedRelations().add(r1);
		follower.getFollowerRelations().add(r1);
		
		userRepository.save(followed)
		userRepository.save(follower)
		
        when:
		1+1
        def followers = userService.findFollowers(follower, new PageParams())

        then:
		true
        followers != null
    }

   
    def "can find followers when signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser followed = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "followed@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser follower1 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "follower1@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser follower2 = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "follower2@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: follower1, followed: followed))
        Relationship r2 = relationshipRepository.save(new Relationship(follower: follower2, followed: followed))
        securityContextService.currentUser() >> follower1
		
		followed.getFollowedRelations().add(r1);
		follower1.getFollowerRelations().add(r1);
		follower2.getFollowerRelations().add(r2);
		
		userRepository.save(followed)
		userRepository.save(follower1)
		userRepository.save(follower2)

        when:
	    def followers = userService.findFollowings(followed, new PageParams())

        then:
		//true
        followers.first().email == "follower1@test.com"
        //followers.first().isMyself == false
        //followers.last().isMyself == true
    }

	
    def "can find a user when not signed in"() {
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
        userDTO.isMyself == null
    }

    def "can find a user when signed in"() {
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        securityContextService.currentUser() >> user

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
	    userDTO != null
		userDTO.isMyself == true

        when:
        MyUser anotherUser = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "another@test.com", password: "secret", name: "another", fname:"zzzzzzzzzzzzzz"))
        userDTO = userService.findOne(anotherUser.id).get()

        then:
	    userDTO != null
		userDTO.isMyself == false
    }
    

}
