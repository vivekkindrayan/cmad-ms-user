package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.RESPONSE_HEADER_CONTENT_TYPE;
import static com.mysocial.util.Constants.RESPONSE_HEADER_JSON;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;


import com.mysocial.beans.Site;
import com.mysocial.db.SitePersistence;
import com.mysocial.util.MySocialUtil;

public class SiteHandler implements Handler<RoutingContext> {
	
	Vertx vertx;
	
	public SiteHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("siteId");
		vertx.executeBlocking(future -> {
			Site s = SitePersistence.getSiteById(id);
			future.complete(s);
		}, resultHandler -> {
			HttpServerResponse response = routingContext.response();
			Site s = (Site) resultHandler.result();
			if (resultHandler.succeeded()) {
				response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON).end(Json.encodePrettily(s));
			} else {
				MySocialUtil.handleFailure(resultHandler, this.getClass());
			}
		});	
	}
}