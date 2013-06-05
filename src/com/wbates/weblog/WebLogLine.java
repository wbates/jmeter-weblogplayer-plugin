package com.wbates.weblog;

import java.util.regex.*;
import com.wbates.time.AccessLogTimeStamp;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class WebLogLine {

  private int status = 0;
  private String path = null;
  private AccessLogTimeStamp timeStamp = null;

  public WebLogLine(String line, JavaSamplerContext context) throws WebLogLineException {
    int TIMESTAMP_FIELD = Integer.parseInt( context.getParameter("FieldTimestamp", "2") );
    int STATUS_FIELD = Integer.parseInt( context.getParameter("FieldStatus", "7") );
    int PATH_FIELD = Integer.parseInt( context.getParameter("FieldPath", "5") );

    Pattern re = Pattern.compile(context.getParameter("LogLinePattern", "([A-Z][a-z]{2} \\d{2} \\d{2}:\\d{2}:\\d{2} [\\w\\-\\.\\_]+ \\w+\\[\\d+\\]: [\\d\\.]+ \\-) \\[(\\d{2}\\/[A-Z][a-z]{2}\\/\\d{4}:\\d{2}:\\d{2}:\\d{2}) [\\+\\-]\\d{4}\\] ([a-zA-Z\\.\\-]+) \\\"([A-Z]+) (\\S+) (HTTP\\/[\\S]+) (\\d{3}) ([\\d\\-]+) \\\"\\S+\\\" (.+)") );
    Matcher m = re.matcher(line);

    if(m.matches() != true) {
      WebLogLineException we = new WebLogLineException("Unable to parse access log line: " + line);
      throw we;
    }

    if(m.group(TIMESTAMP_FIELD).isEmpty()) {
      WebLogLineException we = new WebLogLineException("Timestamp field empty: " + line);
      throw we;
    } else {
      this.timeStamp = new AccessLogTimeStamp(m.group(TIMESTAMP_FIELD), context);
    }

    if(m.group(PATH_FIELD).isEmpty()) {
      WebLogLineException we = new WebLogLineException("Path field empty: " + line);
      throw we;
    } else {
      this.path = m.group(PATH_FIELD);
    }


    if(m.group(STATUS_FIELD).isEmpty()) {
      WebLogLineException we = new WebLogLineException("Status field empty: " + line);
      throw we;
    } else {
      try {
        this.status = Integer.parseInt(m.group(STATUS_FIELD));
      } catch (Exception e) {
        WebLogLineException we = new WebLogLineException("Status field unparseable: " + m.group(STATUS_FIELD));
        throw we;
      }
    }
  }

  public int getStatus() {
    return this.status;
  }

  public AccessLogTimeStamp getTimeStamp() {
    return this.timeStamp;
  }

  public String getPath() {
    return this.path;
  }
}
