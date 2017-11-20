package com.microservice.test.test_app;

import com.microservice.test.test_app.verticle.ProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Launcher {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(2));
		vertx.deployVerticle(new ProducerVerticle());
	}

}
