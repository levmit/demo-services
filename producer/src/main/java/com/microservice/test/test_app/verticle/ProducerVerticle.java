package com.microservice.test.test_app.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class ProducerVerticle extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(ProducerVerticle.class);

	public static final String ACTION_FIELD = "action";
	public static final String VALUE_FIELD = "value";

	public static final String PUT_COMMAND = "put";
	public static final String GET_COMMAND = "get";
	public static final String GET_PRODUCTS_COMMAND = "getProducts";

	public final static String DEFAULT_BUS = "message_bus";

	private int messageIndex;

	public static void main(String[] args) {
		Launcher.executeCommand("run", ProducerVerticle.class.getName());
	}

	@Override
	public void start() throws Exception {
		log.info("Start");

		CircuitBreaker breaker = CircuitBreaker.create("producer-circuit-breaker", vertx,
				new CircuitBreakerOptions().setMaxFailures(5)).fallback(v -> {
					String result = "Circuit is opened executing fallback handler";
					log.info(result);
					return result;
				});

		EventBus eventBus = vertx.eventBus();
		messageIndex = 0;

		long timerId = vertx.setPeriodic(10000, i -> {
			String messageBody = "message number " + messageIndex;
			JsonObject message = createMessage(GET_PRODUCTS_COMMAND, messageBody);

			log.debug("sending: " + message + " to " + DEFAULT_BUS);

			breaker.execute(future -> {
				eventBus.send(DEFAULT_BUS, message, response -> {
					if (response.failed()) {
						String result = "Received failed reply [" + messageIndex + "]: " + response.cause().toString(); 
						log.error(result);
						future.fail(result);
					} else {
						String result = "Received successful reply [" + messageIndex + "]: "
								+ response.result().body().toString();
						log.debug(result);
						future.complete(result);
						messageIndex++;
					}
				});
			});
		});
	}

	private static <T> JsonObject createMessage(String action, T value) {
		JsonObject message = new JsonObject();
		message.put(ACTION_FIELD, action);
		message.put(VALUE_FIELD, value);

		return message;
	}
}