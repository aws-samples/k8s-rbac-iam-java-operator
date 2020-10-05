package com.amazonwebservices.blogs.containers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "io.kubernetes.client.spring.extended.controller,com.amazonwebservices.blogs.containers.config")
@PropertySource(value = {"classpath:vertx.properties"})
public class SpringContainerConfig
{	
	@Autowired
    Environment env;
}


