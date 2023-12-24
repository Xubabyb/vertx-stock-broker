package org.dshid.vertx_stock_broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.dshid.vertx_stock_broker.api.AssetsRestApi;
import org.dshid.vertx_stock_broker.error.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  public static final Integer PORT = 8888;
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error ->
      LOG.error("Unhandled: ", error)
    );
    vertx.deployVerticle(new MainVerticle(), asyncResult ->
    {
      if (asyncResult.failed()) {
        LOG.error("Failed to deploy: ", asyncResult.cause());
        return;
      }
      LOG.info("Deployed {}!", MainVerticle.class.getName());
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Основной объект vert.x Web компонента
    final Router router = Router.router(vertx);

    // Обработчик ошибок
    ErrorHandler.handle(router);

    // Добавляем апи
    AssetsRestApi.attach(router);

    // Создаем Http-сервер
    vertx.createHttpServer()
      .requestHandler(router)
      .exceptionHandler(error -> LOG.error("HTTP Server error", error))
      .listen(PORT, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
