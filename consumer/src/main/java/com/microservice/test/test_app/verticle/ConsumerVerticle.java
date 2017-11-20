package com.microservice.test.test_app.verticle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.test.test_app.context.SpringConfiguration;
import com.microservice.test.test_app.entity.Product;
import com.microservice.test.test_app.service.ProductService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Launcher;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ConsumerVerticle extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(ConsumerVerticle.class);

	public final static String DEFAULT_BUS = "DEFAULT_BUS";

	private final ObjectMapper mapper = Json.mapper;

	private AbstractApplicationContext appContext;
	private ProductService service;

	public static void main(String[] args) {
		Launcher.executeCommand("run", ConsumerVerticle.class.getName());
	}

	@Override
	public void start(Future<Void> startFuture) {
		log.info("Start");

		Future<Void> initializeSteps = initializeApplicationContext().compose(v -> initializeServices());
		initializeSteps.setHandler(ar -> {
			if (ar.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(ar.cause());
			}
		});

		if (initializeSteps.succeeded()) {
			// ****** TODO: Need to be initialized in configuration repository.
			JsonObject config = context.config();
			config.put(DEFAULT_BUS, "message_bus");
			// ***************************************************************

			String address = config.getString(DEFAULT_BUS);
			log.info("Listening to address: " + address);
			vertx.eventBus().<String>consumer(address).handler(doGetProducts());
		} else {
			log.error("Failed to initialize resources");
		}
	}

	private Handler<Message<String>> doGetProducts() {
		log.info("Start doGetProducts");
		return msg -> vertx.<String>executeBlocking(future -> {
			try {
				List<Product> allProducts = service.getAllProducts();
				future.complete(mapper.writeValueAsString(allProducts));
			} catch (JsonProcessingException e) {
				future.fail(e);
			}
		}, result -> {
			if (result.succeeded()) {
				msg.reply(result.result());
			} else {
				msg.reply(result.cause().toString());
			}
		});
	}

	private Future<Void> initializeApplicationContext() {
		Future<Void> future = Future.future();
		appContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		if (appContext != null) {
			future.complete();
		} else {
			future.fail("Failed to initialize Application Context");
		}

		return future;
	}

	private Future<Void> initializeServices() {
		Future<Void> future = Future.future();
		service = appContext.getBean(ProductService.class);
		if (service != null) {
			future.complete();
		} else {
			future.fail("Failed to initialize Product Service");
		}

		return future;
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		appContext.stop();
	}

}