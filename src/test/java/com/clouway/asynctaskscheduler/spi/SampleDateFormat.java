package com.clouway.asynctaskscheduler.spi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class SampleDateFormat implements ParamFormat<Date> {

    @Override
    public Date format(String value) {

      try {
        return new SimpleDateFormat("dd/MM/yyyy").parse(value);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return null;
    }
}
