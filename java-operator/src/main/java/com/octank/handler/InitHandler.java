package com.octank.handler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class InitHandler implements Handler<Future<Void>> {

	private static final Logger logger = LogManager.getLogger(InitHandler.class);

	private final int port = 8080;

	private Vertx vertx;
	private Future<Void> startFuture;
	private HttpServer httpServer;

	public InitHandler() {
	}

	@Autowired
	private PingHandler pingHandler;
	
	@Autowired
	private UptimeHandler healthHandler;
	
	public void init(Vertx vertx, JsonObject config, Future<Void> startFuture) {
		this.vertx = vertx;
		this.startFuture = startFuture;

		// Configure HTTP Server
		HttpServerOptions serverOptions = new HttpServerOptions();
		serverOptions.setCompressionSupported(true);
		serverOptions.setPort(port);
		httpServer = vertx.createHttpServer(serverOptions);
	}

	@Override
	public void handle(Future<Void> event) {
		try {
			logger.info("Setting up routes for REST API endpoints");
			
			// Configure HTTP Routes
			Router router = Router.router(vertx);
					
			router.route(HttpMethod.GET, Routes.PING).handler(pingHandler);
			router.route(HttpMethod.GET, Routes.HEALTH_LIVENESS).handler(healthHandler);
			router.route(HttpMethod.GET, Routes.HEALTH_READINESS).handler(healthHandler);

			httpServer.requestHandler(router);
			httpServer.listen(port);
			logger.info(String.format("HTTP server listening at %d", port));
					
			startFuture.complete();
			logger.info("Completed initializations");
		}
		catch (Exception ex) {
			logger.error("Exception occurred while initializing InitHandler", ex);
		}
	}
}
