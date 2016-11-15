package com.iqmsoft.test.controller

import com.iqmsoft.domain.Micropost
import com.iqmsoft.controller.*
import com.iqmsoft.domain.MyUser
import com.iqmsoft.mongo.repository.MicropostRepository
import com.iqmsoft.mongo.repository.RelationshipRepository;
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.service.MicropostService
import com.iqmsoft.service.MicropostServiceImpl
import com.iqmsoft.security.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MicropostControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository;

    @Autowired
    UserRepository userRepository;
	
	@Autowired
	RelationshipRepository relationRepository;

    MicropostService micropostService;

    SecurityContextService securityContextService = Mock(SecurityContextService);

	Random randomno
	
    @Override
    def controllers() {
		
		
		randomno = new Random();
        micropostService = new MicropostServiceImpl(micropostRepository, userRepository, securityContextService)
        return new MicropostController(micropostRepository, userRepository, micropostService, securityContextService)
    }

    def "can create a micropost"() {
        given:
		
		micropostRepository.deleteAll()
		relationRepository.deleteAll()
		userRepository.deleteAll()
		
        String content = "my content"
		String title = "hello"
        MyUser user = userRepository.save(
			new MyUser(username: "test@test.com", 
			password: "secret", name: "test", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt())))
        securityContextService.currentUser() >> user

        when:
        def response = perform(post("/api/microposts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content: content, title : title)) 
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
				.andExpect(jsonPath('$.type').exists())
				.andExpect(jsonPath('$.title').exists())
                .andExpect(jsonPath('$.content', is(content)))
				.andExpect(jsonPath('$.type', is("plain")))
				.andExpect(jsonPath('$.title', is("hello")))
        micropostRepository.count() == 1
    }

    def "can delete a micropost"() {
        given:
		
		micropostRepository.deleteAll()
		relationRepository.deleteAll()
		userRepository.deleteAll()
		
        MyUser user = userRepository.save(new MyUser(username: "test@test.com", 
			password: "secret", name: "test", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt())))
        
		Micropost micropost = micropostRepository.save(new Micropost(user, "content", "plain", "hello"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(delete("/api/microposts/${micropost.id}"))

        then:
        response.andExpect(status().isOk())
        micropostRepository.count() == 0
    }

}
