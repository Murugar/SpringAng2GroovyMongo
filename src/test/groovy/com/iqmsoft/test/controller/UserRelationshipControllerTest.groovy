package com.iqmsoft.test.controller

import com.iqmsoft.controller.*
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.mongo.repository.MicropostRepository;
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.security.SecurityContextService
import com.iqmsoft.service.UserService
import com.iqmsoft.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import java.util.Random;

class UserRelationshipControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository
	
	@Autowired
	MicropostRepository micropostRepository;

    @Autowired
    RelationshipRepository relationshipRepository

    SecurityContextService securityContextService = Mock(SecurityContextService);
	
	Random randomno = new Random();

    @Override
    def controllers() {
        final UserService userService = new UserServiceImpl(userRepository, securityContextService)
        return new UserRelationshipController(userRepository, userService)
    }

    def "can list followings"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user1 = 
		
		new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz")
        
		MyUser user2 = new 
		MyUser(uid:Math.abs(randomno.nextInt()), username: "satoru@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz")
       
		userRepository.save(user1)
		userRepository.save(user2)
		
		Relationship r1 = new Relationship(follower: user1, followed: user2)
		
		relationshipRepository.save(r1)
        securityContextService.currentUser() >> userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "current@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))

		
		user2.getFollowedRelations().add(r1);
		user1.getFollowerRelations().add(r1);
		userRepository.save([user1, user2])
		
        when:
	    def response = perform(get("/api/users/${user2.uid}/followings"))

        then:
		//true
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].email', is("akira@test.com")))
                .andExpect(jsonPath('$[0].isMyself', is(false)))
                .andExpect(jsonPath('$[0].userStats').exists())
               //.andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
    }

    def "can list followers"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user1 = new 
		MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz")
       
		MyUser user2 = new 
		MyUser(uid:Math.abs(randomno.nextInt()), username: "satoru@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz")
       
		userRepository.save(user1)
		userRepository.save(user2)
		
		Relationship r1 = new Relationship(follower: user2, followed: user1)
        securityContextService.currentUser() >> userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "current@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))

		relationshipRepository.save(r1)
		
		user2.getFollowedRelations().add(r1);
		user1.getFollowerRelations().add(r1);
		userRepository.save([user2, user1])
		
		
        when:
	
        def response = perform(get("/api/users/${user1.uid}/followers"))

        then:
		//true
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].email', is("akira@test.com")))
                .andExpect(jsonPath('$[0].isMyself', is(false)))
                .andExpect(jsonPath('$[0].userStats').exists())
                //.andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
    }
}
