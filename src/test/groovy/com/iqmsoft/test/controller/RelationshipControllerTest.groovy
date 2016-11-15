package com.iqmsoft.test.controller

import com.iqmsoft.controller.*
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.mongo.repository.MicropostRepository;
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.security.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RelationshipControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository
	
	@Autowired
	MicropostRepository micropostRepository;

    @Autowired
    RelationshipRepository relationshipRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

	Random randomno = new Random();
	
    @Override
    def controllers() {
        return new RelationshipController(userRepository, relationshipRepository, securityContextService)
    }

    def "can follow another user"() {
        given:
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        MyUser follower = userRepository.save(new MyUser(username: "test1@test.com", 
			password: "secret", name: "test", fname:"zzzzzzzzzzzzzz",uid:Math.abs(randomno.nextInt())))
        MyUser followed = userRepository.save(new MyUser(username: "test2@test.com", 
			password: "secret", name: "test", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt())))
        securityContextService.currentUser() >> follower

        when:
		
        def response = perform(post("/api/relationships/to/${followed.uid}"))

        then:
        response.andExpect(status().isOk())
        relationshipRepository.count() == 1
    }

    def "can unfollow another user"() {
        given:
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        MyUser follower = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test1@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
        MyUser followed = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test2@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))
        securityContextService.currentUser() >> follower

        when:
        def response = perform(delete("/api/relationships/to/${followed.uid}"))

        then:
        response.andExpect(status().isOk())
        relationshipRepository.count() == 0

        when:
        response = perform(delete("/api/relationships/to/${followed.uid}"))

        then:
        response.andExpect(status().isNotFound())
    }

}
