package com.mysocial.verticles.handlers;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import static com.mysocial.util.Constants.*;

public class ChatHandler implements Handler<ServerWebSocket> {

		Vertx vertx;
		EventBus eventBus;
		
		public ChatHandler(Vertx vertx, EventBus eb) {
			this.vertx = vertx;
			this.eventBus = eb;
		}
		
		public void handle(ServerWebSocket ws) {
			
			eventBus.consumer(EVENT_BUS_ADDRESS, message ->{
				System.out.println("Received message " + message);
			});
			
			ws.closeHandler(new Handler<Void>() {
				@Override
				public void handle(final Void event) {
					System.out.println("Closing chatHandler");
				}
			});
			
			ws.handler(new Handler<Buffer>() {
				@Override
				public void handle(final Buffer data) {
					ObjectMapper m = new ObjectMapper();
					try {
						JsonNode rootNode = m.readTree(data.toString());
						String jsonOutput = m.writeValueAsString(rootNode);
						System.out.println("json generated: " + jsonOutput);
						eventBus.publish(EVENT_BUS_ADDRESS, jsonOutput);
					} catch (IOException e) {
						ws.reject();
					}
				}
			});
			
		}

}
