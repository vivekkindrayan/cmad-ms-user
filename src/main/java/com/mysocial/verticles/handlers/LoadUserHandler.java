package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mysocial.beans.User;
import com.mysocial.util.MySocialUtil;

public class LoadUserHandler implements Handler<RoutingContext> {
	
	Vertx vertx;

	public LoadUserHandler(Vertx vertx) {
		this.vertx = vertx;
	}

	public void handle(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON);
		
		try {
			vertx.executeBlocking(future -> {
				User u = MySocialUtil.getSignedInUser(routingContext);
				future.complete(u);
				}, resultHandler -> {
					if (resultHandler.succeeded()) {
						User u = (User) resultHandler.result();
						if (u != null) {
							response.setStatusCode(HttpResponseStatus.OK.code());
							routingContext.removeCookie(COOKIE_HEADER);
							routingContext.addCookie(Cookie.cookie(COOKIE_HEADER, u.getId().toHexString()));
							ObjectMapper mapper = new ObjectMapper();
							mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
							try {
								String jsonStr = mapper.writeValueAsString(u);
								response.end(jsonStr);
							} catch (Exception e) {
								System.err.println(e.getMessage());
								e.printStackTrace();
								response.end();
							}
						} else {
							response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
							routingContext.removeCookie(COOKIE_HEADER);
							response.end();
						}

					} else {
						response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
						routingContext.removeCookie(COOKIE_HEADER);
						response.end(resultHandler.cause().getMessage());
						MySocialUtil.handleFailure(resultHandler, this.getClass());
					}
				});
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
			routingContext.removeCookie(COOKIE_HEADER);
			response.end();
		}
	}
}
