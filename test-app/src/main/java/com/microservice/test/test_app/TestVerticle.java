package com.microservice.test.test_app;

import io.vertx.core.AbstractVerticle;

public class TestVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    vertx.createHttpServer().requestHandler(request -> {
      request.response().end("TestVerticle is running ....");
    }).listen(8080);
  }
}