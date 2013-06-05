package com.wbates.weblog;

import java.io.*;
import java.util.*;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class WebLogReader {

  private Scanner scanner;
  private JavaSamplerContext context;

  public WebLogReader(JavaSamplerContext c) throws FileNotFoundException, IOException {
    context = c;
    FileInputStream fis = new FileInputStream(context.getParameter("LogFile", "/tmp/apache.log"));
    BufferedInputStream buff = new BufferedInputStream(fis);
    scanner = new Scanner(buff);
  }

  public WebLogLine readLine() throws IOException, WebLogLineException {

    if(this.scanner.hasNext()) {
      return (new WebLogLine(this.scanner.nextLine(), context));
    }

    return null;
  }

}
