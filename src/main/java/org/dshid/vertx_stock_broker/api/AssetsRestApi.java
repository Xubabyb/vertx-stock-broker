package org.dshid.vertx_stock_broker.api;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.dshid.vertx_stock_broker.dto.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static void attach(Router parent) {
    parent.get("/assets")
      .handler(context -> {
        final var response = new JsonArray();
        response
          .add(new Asset("point A"))
          .add(new Asset("point B"))
          .add(new Asset("point C"));
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response().end(response.toBuffer());
      });
  }

}
