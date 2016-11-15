package com.iqmsoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.iqmsoft.auth.StatelessAuthenticationFilter;
import com.iqmsoft.auth.StatelessLoginFilter;
import com.iqmsoft.auth.TokenAuthenticationService;
import com.iqmsoft.service.UserService;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public SecurityConfig(UserService userService,
                          TokenAuthenticationService tokenAuthenticationService) {
        super(true);
        this.userService = userService;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // we use jwt so that we can disable csrf protection
        http.csrf().disable();

        http
                .exceptionHandling().and()
                .anonymous().and()
                .servletApi().and()
                .headers().cacheControl()
        ;

        
        
        http.authorizeRequests()
       
        .antMatchers("/","/public/**", "/resources/**","/resources/public/**","/static/**",
        		"/resources/static/src/**", "/static/src/**", "/resources/static/**").permitAll()
        .antMatchers("/static/src/app/**", "/static/src/css/**", "/static/node_modules/**",
        		"/resources/static/src/app/**", "/resources/static/src/css/**").permitAll()
        .antMatchers("/static/dist/**").permitAll()
        .antMatchers("/static/config/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/users/show/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/users/remove/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/me").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/users/me/microposts").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/microposts/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/relationships/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/feed").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers("/", "/fonts/**").permitAll()
        ;

        http.addFilterBefore(
                new StatelessLoginFilter("/api/login", tokenAuthenticationService, userService, authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(
                new StatelessAuthenticationFilter(tokenAuthenticationService),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userService;
    }
}



