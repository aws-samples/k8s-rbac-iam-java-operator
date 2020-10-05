package com.amazonwebservices.blogs.containers;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonwebservices.blogs.containers.config.SpringContainerConfig;
import com.amazonwebservices.blogs.containers.config.VertxConfig;
import com.amazonwebservices.blogs.containers.kubernetes.ControllerRunner;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Program {
	private static final Logger logger = LogManager.getLogger(Program.class);

	public static void main(String[] args) {
		
		//
		// Instantiating the Spring container using AnnotationConfigApplicationContext
		// In much the same way that Spring XML files are used as input when instantiating a ClassPathXmlApplicationContext, 
		// @Configuration classes may be used as input when instantiating an AnnotationConfigApplicationContext
		//
		VertxConfig appConfig = null;
		ApplicationContext context = null;
		ControllerRunner controllerRunner = null;
		try {
			context = new AnnotationConfigApplicationContext(SpringContainerConfig.class);
			appConfig = (VertxConfig) context.getBean("vertxConfig");
			controllerRunner = (ControllerRunner) context.getBean("controllerRunner");
			logger.info("Completed creating Spring ApplicationContext");
		}
		catch (Exception ex) {
			logger.error("Exception occurred when creating Spring ApplicationContext", ex);
		}
		
		//
		// Create a non-clustered instance of Vertx using the specified options
		//
		VertxOptions vertxOptions = new VertxOptions();
		vertxOptions.setWorkerPoolSize(appConfig.getWorkerPoolSize());
		vertxOptions.setMaxEventLoopExecuteTime(appConfig.getMaxEventLoopExecuteTime());
		vertxOptions.setBlockedThreadCheckInterval(appConfig.getBlockedThreadCheckInterval());
		final Vertx vertx = Vertx.vertx(vertxOptions);
		
		//
		// Create and deploy the verticle that provides the event-loop for all request processing
		//
		WebVerticle verticle = new WebVerticle(context);
		logger.info(String.format("Started deployment of verticle %s", verticle.getClass().getName()));
		vertx.deployVerticle(verticle, asyncResult -> {
			if (asyncResult.succeeded()) {
				logger.info(String.format("Completed deployment of %s with ID %s", verticle.getClass().getName(), asyncResult.result()));
			}
			else {
				logger.error(String.format("Verticle deployment failed; %s", asyncResult.cause()));
			}
		});
		
		controllerRunner.run();
	}
}
