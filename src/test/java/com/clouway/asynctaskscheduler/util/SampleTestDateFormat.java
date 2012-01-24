package com.clouway.asynctaskscheduler.util;

import com.clouway.asynctaskscheduler.spi.ParamFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class SampleTestDateFormat implements ParamFormat<Date> {

    @Override
    public Date parse(String value) {

      try {
        return new SimpleDateFormat("dd/MM/yyyy").parse(value);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return null;
    }

  @Override
  public String format(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy").format(date);
  }
}
