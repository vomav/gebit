package org.gebit.resources;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev-idea")
public class UIResourcesDevIdea implements WebMvcConfigurer {
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/**")
          .addResourceLocations("file:./apps/gebit-app/dist/");
    }
	
	   @Override
	   public void addViewControllers(ViewControllerRegistry registry) {
	       registry.addViewController("/").setViewName("forward:/index.html");
	   }
	
}