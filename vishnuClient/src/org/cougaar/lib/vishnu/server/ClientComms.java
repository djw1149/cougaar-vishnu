// $Header: /opt/rep/cougaar/vishnu/vishnuClient/src/org/cougaar/lib/vishnu/server/Attic/ClientComms.java,v 1.4 2001-02-12 19:35:28 gvidaver Exp $

package org.cougaar.lib.vishnu.server;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.InetAddress;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Contains a bunch of utility routines to allow clients (scheduler,
 * expression compiler, and external clients such as the COUGAAR bridge)
 * to communicate with the web server
 *
 * Copyright (C) 2000 BBN Technologies
 */

public class ClientComms {

  private static String user;
  private static String password;
  private static String host;
  private static String path;
  private static int port;
  private static boolean debugXML = 
    "true".equals (System.getProperty ("org.cougaar.lib.vishnu.server.debugXML"));

  public static String getHost () { return host; }
  
  public static void initialize() {
    String defaultHost = "";
    try {
      defaultHost = InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
    }
    host = System.getProperty ("org.cougaar.lib.vishnu.server.host", defaultHost);
    path = System.getProperty ("org.cougaar.lib.vishnu.server.path",
                               "/~vishnu/");
    user = System.getProperty("org.cougaar.lib.vishnu.server.user", "vishnu");
    password = System.getProperty("org.cougaar.lib.vishnu.server.password", "");
    port = Integer.parseInt (System.getProperty("org.cougaar.lib.vishnu.server.port", "80"));
  }

  public static Map defaultArgs() {
    Map args = new HashMap (7);
    args.put ("user", user);
    args.put ("username", user);
    args.put ("password", password);
    return args;
  }

  /**
   *  General routine to write data to a URL and then read back
   *  the results.
   *  @param args name-value pairs that get attached to the URL
   *  @param pagename does not contain the full path
   *  @return the text string returned by the URL
   */
  public static String postToURL (Map args, String pagename) {
    try {
      Socket socket = new Socket (host, port);
      OutputStream os = socket.getOutputStream();
      String data = convertArgs (args, false);
      String request = "POST " + path + pagename + " HTTP/1.0\r\n" +
        "Content-Type: application/x-www-form-urlencoded\r\n" +
        "Content-Length: " + data.length() + "\r\n\r\n" + data + "\r\n\r\n";
      os.write (request.getBytes());
      InputStream is = socket.getInputStream();
	  StringBuffer sb = new StringBuffer ();
	  byte b[] = new byte[1024];
	  int len;
	  while ((len = is.read(b)) > -1)
		sb.append (new String(b, 0, len));
	  
	  return sb.toString();
    } catch (ConnectException ce) {
      printDiagnostic (ce, null);
    } catch(Exception e) {
      System.err.println (e.getMessage());
      e.printStackTrace();
    }
    return "";
  }

  private static Exception printDiagnostic (Exception e, String stringURL) {
    System.err.println (e.getMessage());
    if (stringURL != null)
      System.out.println
        ("ClientComms.readXML - could not connect to :\n" + stringURL);
    System.out.println
      ("The Java command-line variables you can set are:");
    System.out.println ("-Dorg.cougaar.lib.vishnu.server.host" +
                        " (default = localhost)");
    System.out.println ("-Dorg.cougaar.lib.vishnu.server.path" +
                        " (default = /~vishnu/)");
    System.out.println ("-Dorg.cougaar.lib.vishnu.server.user" +
                        " (default = vishnu)");
    System.out.println ("-Dorg.cougaar.lib.vishnu.server.password" +
                        " (default = \"\")");
    System.out.println ("-Dorg.cougaar.lib.vishnu.server.port" +
                        " (default = 80)");
    return e;
  }

  /**
   *  Routine to read XML data from a URL and parse it.
   *  @param args name-value pairs that get attached to the URL
   *  @param pagename does not contain the full path
   *  @param handler the XML parsing routine
   */
  public static Exception readXML (Map args, String pagename,
                                   DefaultHandler handler) {
      String stringURL = "http://" + host + ":" + port + path + pagename +
	  convertArgs (args, true);
    try {
      if (debugXML) {
	  URL url = new URL (stringURL);
	  System.out.println ("ClientComms.readXML - Only testing URL. No scheduling will take place.");
	  System.out.println ("ClientComms.readXML - url " + url);
	  
	  System.out.println ("ClientComms.readXML - " + testURL (url));
	  return null;
      }
      SAXParser parser = new SAXParser();
      parser.setContentHandler (handler);
      parser.parse (stringURL);
    } catch (ConnectException ce) {
      return printDiagnostic (ce, stringURL);
    } catch (Exception e) {
      System.err.println (e.getMessage());
//      e.printStackTrace();
      try {
	  System.out.println ("readXML - returned html was :\n" +
			      testURL (new URL (stringURL)));
      } catch (Exception ee) {}
      return e;
    }
    return null;
  }

  public static String testURL (URL aURL) {
    try {
      URLConnection connection = aURL.openConnection();
      connection.setDoInput  (true);

      return getResponse (connection);
    } catch (FileNotFoundException fnfe) {
      printDiagnostic (fnfe, aURL.toString());
    } catch(Exception e) {
      System.err.println ("ClientComms.testURL -- \n" + e.getMessage());
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Returns response as string.
   *
   * @param  connection the url connection to get data from
   * @return String reponse from URL
   */

  public static String getResponse(URLConnection connection) throws IOException {
    InputStream is = connection.getInputStream();
    StringBuffer sb = new StringBuffer ();

    byte b[] = new byte[512];
    int len;

    while ((len = is.read(b)) > -1)
      sb.append (new String (b, 0, len));

    return sb.toString ();
  }


  private static String convertArgs (Map args, boolean needqm) {
    Iterator iter = args.keySet().iterator();
    String data = "";
    while (iter.hasNext()) {
      if (! data.equals (""))
        data = data + "&";
      else if (needqm)
        data = "?";
      Object key = iter.next();
      data = data + key + "=" +
        java.net.URLEncoder.encode (args.get(key).toString());
    }
    return data;
  }

}
