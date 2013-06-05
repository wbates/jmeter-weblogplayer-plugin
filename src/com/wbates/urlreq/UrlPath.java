package com.wbates.urlreq;

import java.net.*;
import java.io.*;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public class UrlPath {


  private URL url = null;

  public UrlPath(String u, JavaSamplerContext context) throws MalformedURLException{
    String hostname = context.getParameter("HostName", "localhost");
    String scheme = context.getParameter("Scheme", "http");
    String server = scheme + "://" + hostname;
    url = new URL(server + u);
  }

  public void get() {
    try {
      HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
      conn.connect();
      InputStreamReader content = new InputStreamReader((InputStream) conn.getContent());
      conn.disconnect();
    } catch (IOException ioe) {
      System.err.println("Unable to retrieve URL: " + this.url.toString());
    }

  }

  public String getUrlPathString() {
    return this.url.toString();
  }
}
