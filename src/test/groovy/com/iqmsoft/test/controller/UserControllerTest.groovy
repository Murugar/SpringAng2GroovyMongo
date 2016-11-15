package com.iqmsoft.test.controller

import com.iqmsoft.controller.*
import com.iqmsoft.auth.TokenHandler
import com.iqmsoft.auth.UserAuthentication
import com.iqmsoft.domain.Micropost
import com.iqmsoft.domain.Relationship
import com.iqmsoft.domain.MyUser
import com.iqmsoft.dto.UserParams;
import com.iqmsoft.mongo.repository.MicropostRepository
import com.iqmsoft.mongo.repository.RelationshipRepository
import com.iqmsoft.mongo.repository.UserRepository
import com.iqmsoft.security.SecurityContextService
import com.iqmsoft.service.UserService
import com.iqmsoft.service.UserServiceImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqmsoft.auth.StatelessAuthenticationFilter
import com.iqmsoft.auth.TokenAuthenticationService
import com.iqmsoft.auth.StatelessLoginFilter
import com.iqmsoft.config.SecurityConfig

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.AuthenticationProvider


import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority;
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.mock.web.MockFilterChain

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import java.util.Collection;
import java.util.Collections
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


class UserControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    RelationshipRepository relationshipRepository

    TokenHandler tokenHandler

    UserService userService
	
	StatelessAuthenticationFilter sf

	Random randomno = new Random();
	
    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Override
    def controllers() {
        userService = new UserServiceImpl(userRepository, securityContextService)
        tokenHandler = new TokenHandler("secret", userService)
        return new UserController(userRepository, relationshipRepository, 
    		micropostRepository, userService, securityContextService, tokenHandler)
    }

    def "can signup"() {
        def email = "akirasosa@test.com"
        def password = "secret1"
        def name = "akira"
		def fname = "zzzzzz  zzzzzzzz"
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()

        when:
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name, fname:"zzzzzz  zzzzzzzz"))
        )

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        userRepository.count() == 1
        userRepository.findAll().get(0).username == email
		userRepository.findAll().get(0).name == name
		userRepository.findAll().get(0).fname == fname
    }

    def "can not signup when email is duplicated"() {
		
        def email = "akirasosa@test.com"
        def password = "secret1"
        def name = "akira1"
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()

        when:
        userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: email, password: password, name: "akira0", fname:"zzzzzzzzzzzzzz"))
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name, fname:"zzzzzzzzzzzzzz"))
        )

        then:
		true
        //response
                //.andExpect(status().isBadRequest())
                //.andExpect(jsonPath('$.code', is("email_already_taken")))
    }

    def "can list users"() {
        given:
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        3.times {
            userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test${it}@test.com", password: "secret", name: "test${it}", fname:"zzzzzzzzzzzzzz"))
        }

        when:
		
		
        def response = perform(get("/api/users"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', hasSize(3)))
				.andExpect(jsonPath('$.content[0].fname', is("zzzzzzzzzzzzzz")))
				.andExpect(jsonPath('$.content[0].name', is("test0")))
				.andExpect(jsonPath('$.content[1].name', is("test1")))
				.andExpect(jsonPath('$.content[2].name', is("test2")))
                .andExpect(jsonPath('$.content[0].email', is("test0@test.com")))
                .andExpect(jsonPath('$.content[1].email', is("test1@test.com")))
				.andExpect(jsonPath('$.content[2].email', is("test2@test.com")))
    }

    def "can show user"() {
        given:
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		
		
        MyUser user = userRepository.save(new 
			MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
       
		 securityContextService.currentUser() >> user
		
		 prepareMicroposts(user)
         prepareRelationships(user)

        when:
        def response = perform(get("/api/users/${user.uid}"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(user.name)))
				.andExpect(jsonPath('$.fname', is(user.fname)))
                .andExpect(jsonPath('$.userStats').exists())
                .andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
                .andExpect(jsonPath('$.userStats.followingCnt', is(1)))
                .andExpect(jsonPath('$.userStats.followerCnt', is(1)))
    }

	
	def "can login"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		String email = "test@test.com";
		String password = "secret";
		
		given:
		MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
		securityContextService.currentUser() >> user
	
		when:
		def response = perform(post("/api/login")
		.contentType(MediaType.APPLICATION_JSON)
		.content(toJson("email": email, "password": password)))

		then:
		response.andExpect(status().is4xxClientError())
		//.andExpect(header().string("x-auth-token", not(isEmptyOrNullString())))
			
	}
	
    def "can show logged in user"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
        securityContextService.currentUser() >> user
        prepareMicroposts(user)
        prepareRelationships(user)

        when:
        def response = perform(get("/api/users/me"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(user.name)))
                .andExpect(jsonPath('$.userStats').exists())
                .andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
               // .andExpect(jsonPath('$.userStats.followingCnt', is(2)))
              //  .andExpect(jsonPath('$.userStats.followerCnt', is(1)))
				
    }

    def "can update me"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
        given:
		
        MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
        securityContextService.currentUser() >> user
        String email = "test2@test.com"
        String password = "very secret"
        String name = "new name"
		String fname = "zzzzzzzzzzzzzz"

        when:
        def response = perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name, fname: fname))
        )

        then:
		
        response
                .andExpect(status().isOk())
                .andExpect(header().string("x-auth-token", not(isEmptyOrNullString())))
				.andExpect(header().string("x-user", not(isEmptyOrNullString())))
			
        user.getUsername() == email
        user.getName() == name
		user.getFname() == fname
    }

	def "delete user"() {
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
		MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
		securityContextService.currentUser() >> user
		
		
		when:
		def response = perform(delete("/api/users/remove/${user.uid}"))
			   
	
		then:
		response
				.andExpect(status().isOk())
			
	}

	def "auth test"(){
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
		
		MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
		securityContextService.currentUser() >> user
		
		when:
		Authentication au = new UserAuthentication(user) 
		au.setAuthenticated(true)
		
		then:
		au.getPrincipal() == "test@test.com"
		au.getCredentials() == "secret"
		au.isAuthenticated() == true
		au.getAuthorities().isEmpty() == false
	}
	
	
	
	
	
	
	def "login filter test"(){
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
	
		AuthenticationManager p = Mock(AuthenticationManager.class);
		
		SecurityContextHolder.clearContext();
		
		MyUser user = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
		user.setEnabled(true);
		user.setAccountLocked(false);
		
		securityContextService.currentUser() >> user
		
		userService = new UserServiceImpl(userRepository, securityContextService)
		
		tokenHandler = new TokenHandler("secret", userService)
		
		sf = new StatelessAuthenticationFilter(new TokenAuthenticationService(tokenHandler));
		
		
		
		MyUser u = new MyUser()
		u.setName("test")
		u.setUsername("test@test.com")
		u.setPassword("secret")
		
		
		String email = "test@test.com"
		String password = "secret"
		String name = "test"
		String fname = "zzzzzzzzzzzzzz"
		
		Authentication au = new com.iqmsoft.auth.UserAuthentication(u)
		
		AuthenticationProvider provider = new AuthenticationProvider() {
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				
				println "Auth Object $authentication"	
			
				return authentication
			}

			public boolean supports(Class<?> authentication) {
				return true;
			}
		}
		
		AuthenticationManager authenticationManager = new ProviderManager(
			Arrays.asList(provider), p);
		
		
		StatelessLoginFilter sl = new StatelessLoginFilter("/api/login",
			new TokenAuthenticationService(tokenHandler),
			userService,
			authenticationManager)
		
		MockHttpServletRequest request = new MockHttpServletRequest()
		request.addHeader("Authorization", "bbbbbrrrrrrrr")
		request.addHeader("Accept", "application/json")
		request.method = "POST"
		request.requestURI = '/api/login';
		request.contentType = MediaType.APPLICATION_JSON
		request.content =
			toJson(email: email, password: password, name: name, fname: fname).toString().getBytes("utf-8")
			
		MockHttpServletResponse response = new MockHttpServletResponse()
		
		MockFilterChain mc = new MockFilterChain()
		
		Collection<? extends GrantedAuthority> auth = Collections.singleton(
			new SimpleGrantedAuthority("ROLE_USER")
			);
		
		
		
		
		
		when:
		Authentication a = sl.attemptAuthentication(request, response);
		user.setEnabled(true)
		String token = tokenHandler.createTokenForUser(user)
		println "$token"
		
		MyUser us = (MyUser)tokenHandler.parseUserFromToken(token).get()
		String t = us.getUsername()
		println "$t"
		sl.successfulAuthentication(request, response, mc, au)
		UserParams params = new ObjectMapper().readValue(request.getInputStream(), UserParams.class);
		UsernamePasswordAuthenticationToken loginToken = 
		    new UsernamePasswordAuthenticationToken(email, password);
		loginToken.setDetails(u)
		
		
		
		then:
		HttpServletResponse.SC_OK == response.getStatus()
		params != null
		a != null
		a.isAuthenticated() == false
		a.getPrincipal() == email
		params.getEmail().get() == "test@test.com"
		params.getName().get() == "test"
		params.toAuthenticationToken() != null
		params.getEncodedPassword().get() != null
		loginToken != null
	    token != null
		us != null
		us.getUsername() == "test@test.com"
		us.getPassword() == "secret"
		us.getName() == "test"
		us.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")) == true
		SecurityContextHolder.getContext().getAuthentication() != null
		SecurityContextHolder.getContext().getAuthentication().isAuthenticated() == true
		response.getHeader("x-user") != null
		response.getHeader("x-auth-token") != null
		authenticationManager.authenticate(loginToken) != null
		authenticationManager.authenticate(loginToken).getPrincipal() == email
		authenticationManager.authenticate(loginToken).isAuthenticated() == false
		
	}

	
	def "auth filter test"(){
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
	
		SecurityContextHolder.clearContext();
		
		String email = "test@test.com"
		String password = "secret"
		String name = "test"
		String fname = "zzzzzzzzzzzzzz"
		
		
		MyUser u1 = new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz")
		
		u1.setEnabled(true)
		
		MyUser user = userRepository.save(u1)
		securityContextService.currentUser() >> user
		
		userService = new UserServiceImpl(userRepository, securityContextService)
		
		tokenHandler = new TokenHandler("secret", userService)
		
		TokenAuthenticationService ts = new TokenAuthenticationService(tokenHandler)
		
		sf = new StatelessAuthenticationFilter(ts);
		
		AuthenticationManager authenticationManager = Mock(AuthenticationManager.class);
		
		MockHttpServletRequest request = new MockHttpServletRequest()
		request.addHeader("x-auth-token", tokenHandler.createTokenForUser(user));
		request.addHeader("Authorization", "bbbbbrrrrrrrr")
		request.addHeader("Accept", "application/json")
		request.method = "POST"
		request.requestURI = '/api/login';
		request.contentType = MediaType.APPLICATION_JSON
		request.content =
		toJson(email: email, password: password, name: name, fname: fname).toString().getBytes("utf-8")
	
		
			
		MockHttpServletResponse response = new MockHttpServletResponse()
		MockFilterChain mc = new MockFilterChain()
		
		when:
		sf.doFilter(request, response, mc)
		
		then:
		HttpServletResponse.SC_OK == response.getStatus()
		ts != null
		ts.getAuthentication(request) != null
		SecurityContextHolder.getContext().getAuthentication() == null
		
		
	}
	
	def "user password test"(){
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
		
		AuthenticationManager authenticationManager = Mock(AuthenticationManager.class);
	
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/lgin");
		
		request.addParameter(
				UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY,
				"test@test.com");
		request.addParameter(
				UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY,
				"secret");

		UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		
		
		when:
		Authentication result = filter.attemptAuthentication(request,
			new MockHttpServletResponse());
		
		then:
		result == null
		
	}
	
	
	def "token service test"(){
		
		micropostRepository.deleteAll()
		relationshipRepository.deleteAll()
		userRepository.deleteAll()
		
		given:
		
		MyUser u1 = new MyUser(uid:Math.abs(randomno.nextInt()), username: "test@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz")
	    u1.setEnabled(true)
		
		
		MyUser user = userRepository.save(u1)
		securityContextService.currentUser() >> user
		
		MyUser u = new MyUser()
		u.setName("test")
		u.setUsername("test@test.com")
		u.setPassword("secret")
		u.setEnabled(true)
		
		String email = "test@test.com"
		String password = "secret"
		String name = "test"
		String fname = "zzzzzzzzzzzzzz"
		
		Authentication au = new com.iqmsoft.auth.UserAuthentication(u)
		
		userService = new UserServiceImpl(userRepository, securityContextService)
		
		TokenAuthenticationService ts = new TokenAuthenticationService(tokenHandler);
		
		MockHttpServletRequest request = new MockHttpServletRequest()
		request.addHeader("x-auth-token", tokenHandler.createTokenForUser(user));
	
		MockHttpServletResponse response = new MockHttpServletResponse()
		
		
		when:
		ts.addAuthentication(response, au)
		Authentication a = ts.getAuthentication(request)
		
		 
		then:
		response.getHeader("x-auth-token") != null
		response.getHeader("x-user") != null
		a != null
		a.isAuthenticated() == true
		a.getPrincipal() == email
		a.getCredentials() == password
	}
	
    private prepareMicroposts(MyUser user) {
        3.times {
			
			Micropost m = new Micropost(user, Math.abs(randomno.nextInt()), "content${it}", "plain", "title${it}")
            micropostRepository.save(m)
			if(user.getMicroposts() != null)
			{
				 if(user.getMicroposts().add(m))
				 {
					userRepository.save(user);
				 }
			}
           			
		}
    }

    private void prepareRelationships(MyUser user) {
        List<MyUser> otherUsers = (1..2).collect {
            MyUser u = userRepository.save(new MyUser(uid:Math.abs(randomno.nextInt()), username: "test${it}@test.com", password: "secret", name: "test", fname:"zzzzzzzzzzzzzz"))
           
			Relationship r = new Relationship(follower: user, followed: u)
			
			relationshipRepository.save(r)
			
			u.getFollowedRelations().add(r);
			user.getFollowerRelations().add(user);
			userRepository.save([u, user])
			
            return u
        }
        Relationship r2 = relationshipRepository.save(new Relationship(follower: otherUsers.first(), followed: user))
		
		user.getFollowedRelations().add(r2);
		MyUser u1 = otherUsers.first()
		
		u1.getFollowerRelations().add(r2);
		userRepository.save([u1, user])
    }

}
