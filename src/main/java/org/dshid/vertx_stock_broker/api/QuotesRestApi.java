package org.dshid.vertx_stock_broker.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.dshid.vertx_stock_broker.dto.Asset;
import org.dshid.vertx_stock_broker.dto.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent) {
    final Map<String, Quote> cashedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> cashedQuotes.put(symbol, initRandomQuote(symbol)));

    parent.get("/quotes/:asset").handler(context -> {

      //Переменная пути
      final var assetParam = context.pathParam("asset");
      LOG.debug("Asset parameter: {}", assetParam);

      var optionalQuote = Optional.ofNullable(cashedQuotes.get(assetParam));

      if (optionalQuote.isEmpty()) {
        //Кастомизируем ответ эндпоинта, если мапа не содержит требуемого значения
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "quote for asset " + assetParam + " not available!")
            .put("path", context.normalizedPath()).toBuffer());
        return;
      }

      final JsonObject response = optionalQuote.get().toJsonObject();
      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());
    });

  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
