package com.amazonwebservices.blogs.containers.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class BaseHandler {

	protected void handleResponse (RoutingContext context, AsyncResult<JsonObject> asyncRsult, Future<JsonObject> resultFuture) {
		if (asyncRsult.succeeded()){
			context.response().setStatusCode(200);
			context.response().end(resultFuture.result().encodePrettily());
		}
		else {
			context.response().setStatusCode(400);
			context.response().end(new JsonObject().encodePrettily());
		}
	}
	
	protected void handleResponseArray (RoutingContext context, AsyncResult<JsonArray> asyncRsult, Future<JsonArray> resultFuture) {
		if (asyncRsult.succeeded()){
			context.response().setStatusCode(200);
			context.response().putHeader("Content-Type", "application/json; charset=UTF8");
			context.response().putHeader("Access-Control-Allow-Origin", "*");
			context.response().end(resultFuture.result().encodePrettily());
		}
		else {
			context.response().setStatusCode(400);
			context.response().end(new JsonObject().encodePrettily());
		}
	}
}
