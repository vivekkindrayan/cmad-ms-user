package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import com.mysocial.beans.Company;

import com.mysocial.db.CompanyPersistence;
import com.mysocial.util.MySocialUtil;

public class CompanyHandler implements Handler<RoutingContext> {
	
	Vertx vertx;
	
	public CompanyHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("companyId");
		vertx.executeBlocking(future -> {
			Company c = CompanyPersistence.getCompanyById(id);
			future.complete(c);
		}, resultHandler -> {
			HttpServerResponse response = routingContext.response();
			Company c = (Company) resultHandler.result();
			if (resultHandler.succeeded()) {
				response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(c));
			} else {
				MySocialUtil.handleFailure(resultHandler, this.getClass());
			}
		});
	}
}