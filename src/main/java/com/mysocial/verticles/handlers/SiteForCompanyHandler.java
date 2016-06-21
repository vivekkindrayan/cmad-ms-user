package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import com.mysocial.beans.Site;

import com.mysocial.db.SitePersistence;
import com.mysocial.util.MySocialUtil;

public class SiteForCompanyHandler implements Handler<RoutingContext> {

	Vertx vertx;
	
	public SiteForCompanyHandler (Vertx vertx) {
		this.vertx = vertx;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(RoutingContext routingContext) {
		
		String companyId = routingContext.request().getParam(SitePersistence.KEY_COMPANYID);
		
		vertx.executeBlocking(future -> {
			List<Site> sites = SitePersistence.getSitesForCompany(companyId);
			future.complete(sites);
		}, resultHandler -> {
			HttpServerResponse response = routingContext.response();
			List<Site> sites = (List<Site>) resultHandler.result();
			if (resultHandler.succeeded()) {
				response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(sites));
			} else {
				MySocialUtil.handleFailure(resultHandler, this.getClass());
			}
		});
	}
}