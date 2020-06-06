package com.octank;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.octank.config.SpringContainerConfig;
import com.octank.config.VertxConfig;
import com.octank.kubernetes.ControllerRunner;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Program {
	private static final Logger logger = Logger.getLogger(Program.class);

	public static void main(String[] args) {
		initializeLog4j();
		
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
	
	private static void initializeLog4j() {
		try {
			ClassLoader loader = Program.class.getClassLoader();
			URL url = loader.getResource("log4j.properties");
			InputStream urlStream = url.openConnection().getInputStream();
			Properties log4jProperties = new Properties();
			log4jProperties.load(urlStream);
			PropertyConfigurator.configure(log4jProperties);
			urlStream.close();
			System.out.println(String.format("Initialized Log4J with %s", url.getFile()));
		}
		catch (Exception ex) {
			System.out.println(String.format("Exception occured while trying to initialize Log4J; %s", ex.getMessage()));
		}
	}
}
