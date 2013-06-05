package com.wbates.time;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class AccessLogTimeStamp {

  private String timeStamp;
  private JavaSamplerContext context;

  public AccessLogTimeStamp(String s, JavaSamplerContext c) {
    context = c;
    this.timeStamp = s;
  }

  public long convertToMs() throws AccessLogTimeStampParseException {
    long t;
    Date d;

    DateFormat f = new SimpleDateFormat(context.getParameter("LogTimestampPattern", "dd/MMM/yyyy:HH:mm:ss") );

    try {
      d = f.parse(this.timeStamp);
      t = d.getTime();
    } catch(java.text.ParseException pe) {
      AccessLogTimeStampParseException a = new AccessLogTimeStampParseException("Unable to parse date: " + this.timeStamp + ". Error: " + pe);
      throw a;
    }

    return t;
  }
}
