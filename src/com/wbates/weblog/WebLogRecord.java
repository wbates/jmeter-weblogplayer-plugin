package com.wbates.weblog;

import com.wbates.urlreq.UrlPath;
import com.wbates.time.*;
import java.net.*;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class WebLogRecord {

  private long relTime;
  private UrlPath urlPath = null;
  private int status;

  public WebLogRecord(WebLogLine logline, StaticTime st, JavaSamplerContext context) throws WebLogRecordException {

    long t; // timestamp in milliseconds

    try {
      t = logline.getTimeStamp().convertToMs();
    } catch (AccessLogTimeStampParseException a) {
      WebLogRecordException w = new WebLogRecordException("Unable to convert timestamp. " + a);
      throw w;
    }

    // TODO Is this the right place for this?
    if(st.getFirstTimeStamp()==0) {
      st.setFirstTimeStamp(t);
      this.relTime = 0;
    } else {
      this.relTime = t - st.getFirstTimeStamp();
    }

    String p = logline.getPath();
    try {
      this.urlPath = new UrlPath(p, context);
    } catch (MalformedURLException mue) {
      WebLogRecordException w = new WebLogRecordException("UrlPath not set. " + p + " not a valid URL.");
      throw w;
    }

    this.status = logline.getStatus();
  }

  private void displayRecord() {
    System.out.println(this.relTime + "||" + this.urlPath.getUrlPathString());
  }

  public void retrieve(StaticTime st, JavaSamplerContext context) {

    if(st.getStartTime() == 0) {
      st.setStartTime((new CurrentTime()).now());
    }
    while(this.relTime > (((new CurrentTime()).now() - st.getStartTime()) * (Integer.parseInt(context.getParameter("TimeMultiplier", "1"))) )) {
      try {
        Thread.sleep(100);
      } catch(InterruptedException ie) {
        System.exit(-1);
      }
    }

    this.displayRecord();

    try {
      this.urlPath.get();
    } catch (Exception e) {
      // TODO catch urlPath.get exception
    }
  }

  public int getStatus() {
    return this.status;
  }

}
