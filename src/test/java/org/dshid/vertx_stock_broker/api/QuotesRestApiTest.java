package org.dshid.vertx_stock_broker.api;

import static org.dshid.vertx_stock_broker.MainVerticle.PORT;
import static org.junit.jupiter.api.Assertions.*;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.dshid.vertx_stock_broker.MainVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
class QuotesRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApiTest.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
      testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext testContext) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(PORT));
    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response {}", json);
        assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void returns_not_found_for_unknown_asset(Vertx vertx, VertxTestContext testContext) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(PORT));
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response {}", json);
        assertEquals(404, response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
        testContext.completeNow();
      }));
  }
}
