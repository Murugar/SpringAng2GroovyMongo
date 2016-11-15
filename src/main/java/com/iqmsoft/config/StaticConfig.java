package com.iqmsoft.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
public class StaticConfig extends WebMvcConfigurerAdapter {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/static/src/", 
			"classpath:/static/node_modules/","classpath:/static/dist/","classpath:/static/config/",
			"classpath:/static/src/app/", "classpath:/static/src/app/components/", 
			"classpath:/static/src/css/",
	"classpath:/public/" };

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
		
		/*if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(
					"classpath:/META-INF/resources/webjars/");
		}
		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**").addResourceLocations(
					CLASSPATH_RESOURCE_LOCATIONS);
		}*/
	}

	private static final String[] scripts = {
			"static/dist/0.chunk.js",
			"static/dist/1.chunk.js",
			"static/dist/2.chunk.js",
			"static/dist/3.chunk.js",
			"static/dist/4.chunk.js",
			"static/dist/5.chunk.js", 
			"static/dist/6.chunk.js",
	        "static/src//polyfill.js",
	        "static/src/main.js",
	        "static/src/vendor.js"
	};
	
	@Bean
    public ViewResolver getViewResolver() {
		
		 ScriptTemplateViewResolver viewResolver = new ScriptTemplateViewResolver();
	        viewResolver.setPrefix("/BOOT-INF/classes/static/src/");
	        viewResolver.setSuffix(".js");
	        return viewResolver;
		
        //InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        //resolver.setPrefix("/BOOT-INF/classes/static/src");
       // resolver.setSuffix(".html");
        //return resolver;
    }
	
	@Bean
    public ScriptTemplateConfigurer reactConfigurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
       // configurer.setEngineName("nashorn");

		/* Initialise the ScriptEngine with these scripts. */
        configurer.setScripts(scripts);

		/* The ScriptEngine will call this function to perform the render */
        configurer.setRenderFunction("render");

		/* We cannot share a ScriptEngine between threads when rendering React applications */
       //configurer.setSharedEngine(false);

        return configurer;
    }
	
}
