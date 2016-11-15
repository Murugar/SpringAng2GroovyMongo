package com.iqmsoft.test.controller

import com.iqmsoft.controller.*
import com.iqmsoft.domain.Micropost
import com.iqmsoft.domain.MyUser
import com.iqmsoft.mongo.repository.MicropostRepository
import com.iqmsoft.mongo.repository.RelationshipRepository;
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.service.MicropostService
import com.iqmsoft.service.MicropostServiceImpl
import com.iqmsoft.security.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import java.util.Random;

class UserMicropostControllerTest extends BaseControllerTest {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private MicropostRepository micropostRepository
	
	@Autowired
	RelationshipRepository relationRepository;

    SecurityContextService securityContextService = Mock(SecurityContextService)

	Random randomno = new Random();
	
	
    @Override
    def controllers() {
        final MicropostService micropostService = new MicropostServiceImpl(micropostRepository, userRepository, securityContextService);
        return new UserMicropostController(userRepository, micropostService, securityContextService)
    }

    def "can list microposts"() {
		
		micropostRepository.deleteAll()
		relationRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
       
		Micropost m = new Micropost(user: user, content: "my content1", type : "plain", title : "hello")
		
		micropostRepository.save(m)

		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}
		
		
        when:
        def response = perform(get("/api/users/${user.uid}/microposts/"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("my content1")))
				.andExpect(jsonPath('$[0].type', is("plain")))
				.andExpect(jsonPath('$[0].title', is("hello")))
                .andExpect(jsonPath('$[0].isMyPost', nullValue()))
    }

    def "can list my microposts"() {
		
		
		micropostRepository.deleteAll()
		relationRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        
		Micropost m = new Micropost(user: user, content: "my content1", type : "plain", title : "hello")
		
		micropostRepository.save(m)
        securityContextService.currentUser() >> user
		
		
		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}

        when:
        def response = perform(get("/api/users/me/microposts/"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("my content1")))
				.andExpect(jsonPath('$[0].type', is("plain")))
				.andExpect(jsonPath('$[0].title', is("hello")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
    }

}
