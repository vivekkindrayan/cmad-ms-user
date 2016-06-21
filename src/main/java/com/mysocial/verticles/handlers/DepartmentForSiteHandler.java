package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import com.mysocial.beans.Department;
import com.mysocial.db.DepartmentPersistence;
import com.mysocial.util.MySocialUtil;

public class DepartmentForSiteHandler implements Handler<RoutingContext> {
	
	Vertx vertx;
	
	public DepartmentForSiteHandler (Vertx vertx) {
		this.vertx = vertx;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(RoutingContext routingContext) {
		String siteId = routingContext.request().getParam(DepartmentPersistence.KEY_SITEID);
		vertx.executeBlocking(future -> {
			List<Department> departments = DepartmentPersistence.getDepartmentsForSite(siteId);
			future.complete(departments);
		}, resultHandler -> {
			HttpServerResponse response = routingContext.response();
			List<Department> departments = (List<Department>) resultHandler.result();
			if (resultHandler.succeeded()) {
				response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(departments));
			} else {
				MySocialUtil.handleFailure(resultHandler, this.getClass());
			}
		});
		
	}
}