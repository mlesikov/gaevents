package com.clouway.asynctaskscheduler.gae;

import com.clouway.asynctaskscheduler.spi.EventTransport;
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GsonEventTransport implements EventTransport {

  private final Gson gson;

  @Inject
  public GsonEventTransport(Gson gson) {
    this.gson = gson;
  }

  @Override
  public <T> T in(Class<T> eventClass, InputStream inputStream) {
    return gson.fromJson(new InputStreamReader(inputStream), eventClass);
  }

  @Override
  public <T> void out(Class<? extends T> eventClass, T event, OutputStream outputStream) {

    String json = gson.toJson(event, eventClass);

    try {
      outputStream.write(json.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
