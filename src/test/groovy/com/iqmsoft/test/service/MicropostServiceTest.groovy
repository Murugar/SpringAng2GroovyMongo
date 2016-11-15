package com.iqmsoft.test.service

import com.iqmsoft.security.*
import com.iqmsoft.exception.*
import com.iqmsoft.service.*
import com.iqmsoft.domain.Micropost
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.dto.PageParams
import com.iqmsoft.dto.PostDTO
import com.iqmsoft.mongo.repository.MicropostRepository
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.mongo.repository.UserRepository

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

class MicropostServiceTest extends BaseServiceTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Shared
    MicropostService micropostService

    @Autowired
    RelationshipRepository relationshipRepository
	
	Random randomno = new Random();

    def setup() {
        micropostService = new MicropostServiceImpl(micropostRepository, userRepository, securityContextService)
    }

    def "can delete micropost when have a permission"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test", type : "plain", title: "hello"))
        securityContextService.currentUser() >> user
		println "$user"
	    when:
        micropostService.delete(post.id)
		println "Deleted with permission"

        then:
        micropostRepository.count() == 0
    }

  def "can not delete micropost when have no permission"() {
	  
	  
	  micropostRepository.deleteAll()
	  relationshipRepository.deleteAll()
	  userRepository.deleteAll()
	  
        given:
        MyUser user = userRepository.save(new MyUser(username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test", type: "plain", title: "hello"))

        when:
        micropostService.delete(post.id)
		println "Deleted with No permission"

        then:
        thrown(NotPermittedException)
		println "Not Deleted with No permission"
        micropostRepository.count() == 1
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "can find posts as feed"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
       
		Micropost m = new Micropost(user: user, content: "my content", type: "plain", title: "hello")
		micropostRepository.save(m)
        securityContextService.currentUser() >> user

		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}
		
		
        MyUser followed = userRepository.save(new MyUser(username: "test1@test.com", password: "secret", name: "test1", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
        relationshipRepository.save(new Relationship(follower: user, followed: followed))
		Micropost m1 = new Micropost(user: followed, content: "follower content", type: "plain", title: "hello")
		micropostRepository.save(m1)

		if(followed.getMicroposts() != null)
		{
			 if(followed.getMicroposts().add(m1))
			 {
				this.userRepository.save(user);
			 }
		}
		
		
        when:
        List<PostDTO> posts = micropostService.findAsFeed(new PageParams())

        then:
        posts.first().content == 'my content'
		posts.first().type == 'plain'
		posts.first().title == 'hello'
        posts.first().isMyPost == true
        //posts.last().isMyPost == true
    }

    def "can find posts by user when not signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
       
		Micropost m = new Micropost(user: user, content: "my content", type: "plain", title: "hello")
		micropostRepository.save(m)

		
		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}
		
        when:
        List<PostDTO> posts = micropostService.findByUser(user, new PageParams())

        then:
        posts.first().isMyPost == null;
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "can find posts by user when signed in"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
        securityContextService.currentUser() >> user
		
		Micropost m = new Micropost(user: user, content: "my content", type: "plain", title: "hello")
		micropostRepository.save(m)
	   
		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(m))
			 {
				this.userRepository.save(user);
			 }
		}

        when:
		List<PostDTO> posts = micropostService.findByUser(user, new PageParams())

        then:
        posts.first().isMyPost == true;
		posts.first().type == 'plain'
		posts.first().title == 'hello'

        when:
        MyUser anotherUser = userRepository.save(new MyUser(username: "satoru@test.com", password: "secret", name: "satoru", fname:"zzzzzzzzzzzzzz", uid:Math.abs(randomno.nextInt()),))
       
		Micropost m1 = new Micropost(user: anotherUser, content: "my content", type: "plain", title: "hello")
	    micropostRepository.save(m1)
       
		
		if(anotherUser .getMicroposts() != null)
		{
			 if(anotherUser .getMicroposts().add(m1))
			 {
				this.userRepository.save(anotherUser);
			 }
		}

		List<PostDTO> anotherPosts = micropostService.findByUser(anotherUser, new PageParams())
		
        then:
        anotherPosts.first().isMyPost == false;
		posts.first().type == 'plain'
		posts.first().title == 'hello'
    }
    
}
