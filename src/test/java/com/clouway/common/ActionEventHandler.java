package com.clouway.common;

import com.clouway.asynctaskscheduler.spi.AsyncEventHandler;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ActionEventHandler implements AsyncEventHandler {
  public static String message;

  public void onAction(ActionEvent event) {
      message = event.getMessage();
    }
}
