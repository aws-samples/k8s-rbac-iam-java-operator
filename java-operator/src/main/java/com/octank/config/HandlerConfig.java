package com.octank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.octank.handler.InitHandler;
import com.octank.handler.PingHandler;
import com.octank.handler.UptimeHandler;

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
