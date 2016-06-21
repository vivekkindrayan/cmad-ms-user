package com.mysocial.verticles.handlers;

import static com.mysocial.util.Constants.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysocial.beans.Auth;
import com.mysocial.beans.User;
import com.mysocial.db.UserPersistence;
import com.mysocial.util.MySocialUtil;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

public class LoginHandler implements Handler<RoutingContext> {

	Vertx vertx;
	
	public LoginHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void handle(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		response.putHeader(RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_JSON);
		
		try {
		
			String jsonStr = routingContext.getBodyAsString();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Auth auth = mapper.readValue(jsonStr, Auth.class);
			System.out.println("Authenticating user with name " + auth.getUserName());
			
			vertx.executeBlocking(future -> {
				Boolean authenticated = UserPersistence.authenticateUser(auth.getUserName(), auth.getPassword());
				future.complete(authenticated);
			}, resultHandler -> {
				if (resultHandler.succeeded()) {
					Boolean authenticated = (Boolean) resultHandler.result();
					if (authenticated) {
						User u = UserPersistence.getUserByUsername(auth.getUserName());
						System.out.println("Authenticated successfully");
						routingContext.removeCookie(COOKIE_HEADER);
						routingContext.addCookie(Cookie.cookie(COOKIE_HEADER, u.getId().toHexString()));
						response.setStatusCode(HttpResponseStatus.OK.code());
					} 
					else {
						System.err.println("Authentication failed");
						routingContext.removeCookie(COOKIE_HEADER);
						response.setStatusCode(HttpResponseStatus.FORBIDDEN.code());
					}
					response.end();
				} 
				else {
					response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
					routingContext.removeCookie(COOKIE_HEADER);
					response.end(resultHandler.cause().getMessage());
					MySocialUtil.handleFailure(resultHandler, this.getClass());
				}
			});
			
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
			routingContext.removeCookie(COOKIE_HEADER);
			response.end();
		}
	}
}
