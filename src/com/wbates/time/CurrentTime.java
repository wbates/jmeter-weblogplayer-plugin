package com.wbates.time;

import java.util.Calendar;

/**
 *
 * @author bbates
 */
public class CurrentTime {
  public CurrentTime() {

  }

  public long now() {
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }
}
