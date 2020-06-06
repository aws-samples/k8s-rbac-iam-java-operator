package com.octank.kubernetes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.octank.handler.InitHandler;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.DefaultController;
import io.kubernetes.client.informer.SharedInformerFactory;

public class ControllerRunner {

	private static final Logger logger = LogManager.getLogger(InitHandler.class);

	List<Controller> controllers = new ArrayList<Controller>();
	SharedInformerFactory informerFactory;

	public ControllerRunner(SharedInformerFactory sharedInformerFactory, 
			Controller iamGroupCrdController) {
		this.informerFactory = sharedInformerFactory;
		this.controllers.add(iamGroupCrdController);
	}

	public void run() {
		logger.info("Starting all SharedInformers");
		informerFactory.startAllRegisteredInformers();
		
		logger.info("Running all Controllers");
		for (Controller controller : controllers) {
			DefaultController controllerImpl = (DefaultController) controller;
			logger.info(String.format("Starting controller %s", controllerImpl.getName()));
			Executors.newSingleThreadExecutor().execute(()->controller.run());
		}
	}
}
