package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import com.mysocial.beans.Department;
import com.mysocial.db.DepartmentPersistence;
import com.mysocial.util.MySocialUtil;

public class DepartmentHandler implements Handler<RoutingContext> {
	
	Vertx vertx;
	
	public DepartmentHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("deptId");
		vertx.executeBlocking(future -> {
			Department d = DepartmentPersistence.getDepartmentById(id);
			future.complete(d);
		}, resultHandler -> {
			HttpServerResponse response = routingContext.response();
			Department d = (Department) resultHandler.result();
			if (resultHandler.succeeded()) {
				response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(d));
			} else {
				MySocialUtil.handleFailure(resultHandler, this.getClass());
			}			
		});
	}
}