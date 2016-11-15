package com.iqmsoft.test.controller

import java.util.ArrayList
import java.util.List
import java.util.Random
import com.iqmsoft.domain.Micropost
import com.iqmsoft.domain.MyUser
import com.iqmsoft.mongo.repository.MicropostRepository
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.service.MicropostService
import com.iqmsoft.service.MicropostServiceImpl
import com.iqmsoft.security.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FeedControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository;

    @Autowired
    UserRepository userRepository;
	
	@Autowired
	RelationshipRepository relationRepository;

    SecurityContextService securityContextService = Mock(SecurityContextService);

	Random randomno = new Random();
	
    def "can show feed"() {
        given:
		
		micropostRepository.deleteAll()
		relationRepository.deleteAll()
		userRepository.deleteAll()
		
		
		
        MyUser user = userRepository.save(new MyUser(username: "test1@test.com", 
			password: "secret", name: "test", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt())))
		
		
		//List<Micropost> m = new ArrayList<>()
		//m.add(new Micropost(user: user, content: "content0", type: "plain", title : "hello"))
		//m.add(new Micropost(user: user, content: "content1", type: "plain", title : "hello"))
		//m.add(new Micropost(user: user, content: "content2", type: "plain", title : "hello"))
		//m.add(new Micropost(user: user, content: "content3", type: "plain", title : "hello"))
	
		
		Micropost m = new Micropost(user: user, content: "content0", type: "plain", title : "hello")
		
		micropostRepository.save(m)
		
		
		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}
		
        securityContextService.currentUser() >> user

        when:
        def response = perform(get("/api/feed"))

        then:
		
	
		
        response
             //   .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("content0")))
				.andExpect(jsonPath('$[0].type', is("plain")))
				.andExpect(jsonPath('$[0].title', is("hello")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
               // .andExpect(jsonPath('$[0].createdAt').exists())
                .andExpect(jsonPath('$[0].user.email', is("test1@test.com")))
				.andExpect(jsonPath('$[0].user.fname', is("zzzzzzzzzzzzzz")))
				.andExpect(jsonPath('$[0].user.name', is("test")))
    }

    @Override
    def controllers() {
        MicropostService micropostService = new MicropostServiceImpl(micropostRepository, userRepository, securityContextService)
        return new com.iqmsoft.controller.FeedController(micropostService)
    }
}
