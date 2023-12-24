package org.dshid.vertx_stock_broker.error;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

  public static void handle(Router parent) {
    parent.route()
      .failureHandler(errorContext -> {
        if (errorContext.response().ended()) {
          return;
        }
        LOG.error("Route error: ", errorContext.failure());
        errorContext.response()
          .setStatusCode(500)
          .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
      });
  }

}
