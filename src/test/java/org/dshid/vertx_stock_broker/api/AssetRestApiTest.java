package org.dshid.vertx_stock_broker.api;

import static org.dshid.vertx_stock_broker.MainVerticle.PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class AssetRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(AssetRestApiTest.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(),
      testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_all_assets(Vertx vertx, VertxTestContext testContext) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(PORT));
    client.get("/assets")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonArray();
        LOG.info("Response {}", json);
        assertEquals("[{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"},{\"name\":\"AAPL\"}]", json.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }
}
