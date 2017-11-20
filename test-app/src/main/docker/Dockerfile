FROM vertx/vertx3

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_NAME com.microservice.test.test_app.TestVerticle

COPY ./verticles $VERTICLE_HOME

ENV VERTX_OPTIONS "-cluster" 

ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]