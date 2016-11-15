package com.iqmsoft.test.repository

import com.iqmsoft.mongo.repository.*
import com.iqmsoft.domain.Micropost
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.dto.PageParams
import com.iqmsoft.dto.PostDTO

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired

class MicropostRepositoryTest extends BaseRepositoryTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository
	
	Random randomno = new Random();

    def "can find feed"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser follower = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        MyUser followed = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test1@test.com", password: "secret", name: "test1", fname:"zzzzzzzzzzzzzz"))
        MyUser another = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test2@test.com", password: "secret", name: "test2", fname:"zzzzzzzzzzzzzz"))
      
		relationshipRepository.save(new Relationship(follower: follower, followed: followed))
        [follower, followed, another].each { u ->
			
			Micropost m = new Micropost(content: "test1", uid:Math.abs(randomno.nextInt()), user: u, type: "plain", title: "hello")
			
			micropostRepository.save(m)
			//micropostRepository.save(new Micropost(content: "test2", user: u, type: "plain", title: "hello"))

			
			if(u.getMicroposts() != null)
			{
				 if(u.getMicroposts().add(m))
				 {
					this.userRepository.save(u);
				 }
			}
			
          }

        when:
        List<PostDTO> result = micropostRepository.findAsFeed(follower, new PageParams())

        then:
        result.size() == 1
        //result.first().user.id == followed.id
       // result.last().user.id == follower.id
    }

    def "can find feed by since_id or max_id"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", uid:Math.abs(randomno.nextInt()), user: user, type: "plain", title: "hello"))
      //  Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user, type: "plain", title: "hello"))
      //  Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user, type: "plain", title: "hello"))

		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(post1))
			 {
				this.userRepository.save(user);
			 }
		}
		
		
        when:
        List<PostDTO> result = micropostRepository.
		findAsFeed(user, new PageParams(sinceId: post1.uid))

        then:
        result.size() == 1
      //  result.first().getUid() == post1.uid

        when:
        result = micropostRepository.findAsFeed(user, new PageParams(maxId: post1.uid))

        then:
        result.size() == 1
        //result.first().id == post1.id
    }

    def "can find posts by user"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "akira@test.com", password: "secret", name: "akira", fname:"zzzzzzzzzzzzzz"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", uid:Math.abs(randomno.nextInt()),
			user: user, type: "plain", title: "hello"))
     //   Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user, type: "plain", title: "hello"))
     //   Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user, type: "plain", title: "hello"))

		if(user.getMicroposts() != null)
		{
			 if(user.getMicroposts().add(post1))
			 {
				this.userRepository.save(user);
			 }
		}
		
		
        when:
        List<Micropost> result = micropostRepository.findByUser(user, new PageParams(sinceId: post1.uid))

        then:
        result.size() == 1
        //result.first() == post3

        when:
        result = micropostRepository.findByUser(user, new PageParams(maxId: post1.uid))

        then:
        result.size() == 1
        //result.first() == post1
    }

}
