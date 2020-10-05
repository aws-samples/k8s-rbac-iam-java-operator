package com.amazonwebservices.blogs.containers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component (value = "vertxConfig")
public class VertxConfig {
	
	@Value("${vertx.max.worker.threads}")
	private int workerPoolSize;

	@Value("${vertx.max.eventloop.execute.time}")
	private int maxEventLoopExecuteTime;
	
	@Value("${vertx.max.blocked.thread.check.interval}")
	private int blockedThreadCheckInterval;

	public int getWorkerPoolSize() {
		return workerPoolSize;
	}

	public int getMaxEventLoopExecuteTime() {
		return maxEventLoopExecuteTime;
	}

	public int getBlockedThreadCheckInterval() {
		return blockedThreadCheckInterval;
	}
}