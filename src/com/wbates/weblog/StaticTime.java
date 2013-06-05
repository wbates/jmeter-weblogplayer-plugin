package com.wbates.weblog;

public class StaticTime {
  private long firstTS = 0;
  private long starttime = 0;

  public StaticTime() {

  }

  public void setFirstTimeStamp(long l) {
    firstTS = l;
  }

  public long getFirstTimeStamp() {
    return firstTS;
  }

  public void setStartTime(long l) {
    starttime = l;
  }

  public long getStartTime() {
    return starttime;
  }

}
