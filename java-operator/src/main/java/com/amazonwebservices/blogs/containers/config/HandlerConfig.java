package com.amazonwebservices.blogs.containers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonwebservices.blogs.containers.handler.InitHandler;
import com.amazonwebservices.blogs.containers.handler.PingHandler;
import com.amazonwebservices.blogs.containers.handler.UptimeHandler;

@Configuration
public class HandlerConfig {

	@Bean
	public UptimeHandler healthHandler() {
		return new UptimeHandler();
	}

	@Bean
	public InitHandler initHandler() {
		return new InitHandler();
	}
	
	@Bean
	public PingHandler pingHandler() {
		return new PingHandler();
	}
}
