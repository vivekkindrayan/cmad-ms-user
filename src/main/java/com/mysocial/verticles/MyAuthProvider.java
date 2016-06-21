package com.mysocial.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class MyAuthProvider implements AuthProvider {

	@Override
	public void authenticate(JsonObject arg0, Handler<AsyncResult<User>> arg1) {
	
	}

}
