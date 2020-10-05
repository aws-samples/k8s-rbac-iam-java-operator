package com.amazonwebservices.blogs.containers;

import org.springframework.context.ApplicationContext;

import com.amazonwebservices.blogs.containers.handler.InitHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class WebVerticle extends AbstractVerticle {

	private InitHandler initHandler;
	
	/**
	 * This is not an elegant way to inject a dependency. 
	 * Unfortunately, we can't turn this Verticle into a Spring Bean and let Spring manage its life cycle. 
	 * A Verticle has to be created and initialized by the Vertx instance using a DeploymentManager.
	 */
	public WebVerticle(final ApplicationContext context) {
		initHandler = (InitHandler) context.getBean("initHandler");
	}
	
	public void start(Future<Void> startFuture){
		initHandler.init(vertx, config(), startFuture);
		Handler<Future<Void>> blockingCodeHandler = initHandler;
		Handler<AsyncResult<Void>> resultHandler = asyncResult -> {};
		vertx.executeBlocking(blockingCodeHandler, resultHandler);
	}
}
