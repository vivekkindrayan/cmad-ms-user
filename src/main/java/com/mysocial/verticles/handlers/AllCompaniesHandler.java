package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import com.mysocial.beans.Company;
import com.mysocial.db.CompanyPersistence;
import com.mysocial.util.MySocialUtil;

public class AllCompaniesHandler implements Handler<RoutingContext> {
	
	Vertx vertx;
	
	public AllCompaniesHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(RoutingContext routingContext) {
		
		vertx.executeBlocking(future -> {
				List<Company> companies = CompanyPersistence.getAllCompanies();
				future.complete(companies);
			}, resultHandler -> {
				HttpServerResponse response = routingContext.response();
				List<Company> companies = (List<Company>) resultHandler.result();
				if (resultHandler.succeeded()) {
					response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(companies));
				} else {
					MySocialUtil.handleFailure(resultHandler, this.getClass());
				}
		});
	}
}

