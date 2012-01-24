package com.clouway.asynctaskscheduler.spi;

/**
 * Presents a simple parameter format which is used for custom parsing of params
 * Example usage ;
 * <p/>
 * AsyncTaskParams params = AsyncTaskParams.with("date", "10/11/2011");
 * <p/>
 * params.format("date", SampleDateFormat.class)
 * <p/>
 * where    SampleDateFormat   is example implementation of a Param Format interface
 * <p/>
 * public class SampleDateFormat implements ParamFormat<Date> {
 * <p/>
 * {@code
 *
 *
 * @Override public Date format(String value) {
 * <p/>
 * try {
 * return new SimpleDateFormat("dd/MM/yyyy").parse(value);
 * } catch (ParseException e) {
 * e.printStackTrace();
 * }
 * return null;
 * }
 * }
 * }
 * <p/>
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public interface ParamFormat<T> {
  /**
   * @param value
   * @return
   */
  T format(String value);
}