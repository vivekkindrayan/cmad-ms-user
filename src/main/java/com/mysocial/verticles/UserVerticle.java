package com.mysocial.verticles;

import static com.mysocial.util.Constants.*;

import com.mysocial.verticles.handlers.AllCompaniesHandler;
import com.mysocial.verticles.handlers.AllDepartmentsHandler;
import com.mysocial.verticles.handlers.AllSitesHandler;
import com.mysocial.verticles.handlers.ChatHandler;
import com.mysocial.verticles.handlers.CompanyHandler;
import com.mysocial.verticles.handlers.DepartmentForSiteHandler;
import com.mysocial.verticles.handlers.DepartmentHandler;
import com.mysocial.verticles.handlers.LoadUserHandler;
import com.mysocial.verticles.handlers.LoginHandler;
import com.mysocial.verticles.handlers.RegisterHandler;
import com.mysocial.verticles.handlers.SiteForCompanyHandler;
import com.mysocial.verticles.handlers.SiteHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.ErrorHandler;

public class UserVerticle extends AbstractVerticle {

	public static final String VERTICLE_NAME = UserVerticle.class.getName();
	public static final int HTTP_PORT = 7001;
	
	@Override
	public void start(Future<Void> startFuture)
	{
		System.out.println(VERTICLE_NAME + " started");
		startFuture.complete();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void stop(Future stopFuture) throws Exception
	{
		System.out.println(VERTICLE_NAME + " stopped");
		stopFuture.complete();
	}
	
	public void deploy(Vertx vertx, Router router, HttpServer server, EventBus eventBus) throws Exception
	{
		vertx.deployVerticle(VERTICLE_NAME, new Handler<AsyncResult<String>>() {
			public void handle(AsyncResult<String> event) {
				
				router.get(REST_URL_PREFIX + REST_URL_GET_COMPANIES).handler(new AllCompaniesHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_COMPANY).handler(new CompanyHandler(vertx));
				
				router.get(REST_URL_PREFIX + REST_URL_GET_DEPARTMENTS).handler(new AllDepartmentsHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_DEPARTMENT).handler(new DepartmentHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_DEPTS_FOR_SITES).handler(new DepartmentForSiteHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_SITES).handler(new AllSitesHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_SITE).handler(new SiteHandler(vertx));
				router.get(REST_URL_PREFIX + REST_URL_GET_SITES_FOR_COMPANY).handler(new SiteForCompanyHandler(vertx));
				
				router.post(REST_URL_PREFIX + REST_URL_REGISTER).handler(new RegisterHandler(vertx));
				router.post(REST_URL_PREFIX + REST_URL_LOGIN).handler(new LoginHandler(vertx, eventBus));
				router.get(REST_URL_PREFIX + REST_URL_LOAD_SIGNED_IN_USER).handler(new LoadUserHandler(vertx));

				server.websocketHandler(new ChatHandler(vertx, eventBus)).listen(HTTP_PORT);
				
				System.out.println(VERTICLE_NAME + " deployment complete");
			}
		});
	}
	
	public static void main(String[] args) throws Exception 
	{
		VertxOptions options = new VertxOptions().setWorkerPoolSize(DEFAULT_WORKER_POOL_SIZE);
		Vertx vertx = Vertx.vertx(options);
		HttpServer server = vertx.createHttpServer();
		EventBus eventBus = vertx.eventBus();
		Router router = Router.router(vertx);
		
		router.route().handler(CookieHandler.create());
		router.route().handler(BodyHandler.create());
		router.route().failureHandler(ErrorHandler.create());
		
		UserVerticle uv = new UserVerticle();
		uv.deploy(vertx, router, server, eventBus);
		server.requestHandler(router::accept).listen(HTTP_PORT);
	}
}
