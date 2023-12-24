package org.dshid.vertx_stock_broker.dto;

public class Asset {

  private final String name;

  public Asset(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
