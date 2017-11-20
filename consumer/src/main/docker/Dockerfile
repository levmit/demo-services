FROM vertx/vertx3

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_NAME com.microservice.test.test_app.verticle.ConsumerVerticle

COPY ./verticles $VERTICLE_HOME

ENV VERTX_OPTIONS "-cluster -ha" 

ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME $VERTX_OPTIONS -cp $VERTICLE_HOME/*"]