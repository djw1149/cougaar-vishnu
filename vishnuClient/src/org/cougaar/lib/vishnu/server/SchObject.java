// $Header: /opt/rep/cougaar/vishnu/vishnuClient/src/org/cougaar/lib/vishnu/server/Attic/SchObject.java,v 1.4 2001-04-12 17:50:31 dmontana Exp $

package org.cougaar.lib.vishnu.server;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

/**
 * For any object (task, resource, or global), stores the mapping between
 * field name and value to allow look up of data in fields.
 *
 * This software is to be used in accordance with the COUGAAR license
 * agreement. The license agreement and other information can be found at
 * http://www.cougaar.org.
 *
 * Copyright 2001 BBNT Solutions LLC
 */

public class SchObject {

  protected HashMap data = new HashMap();
  private String key;
  protected TimeOps timeOps;
  private static boolean debug = 
    ("true".equals (System.getProperty ("vishnu.debug")));

  public SchObject (TimeOps timeOps) {
    this.timeOps = timeOps;
  }

  public void addField (String name, String type, Object value,
                        boolean iskey, boolean isListField) {
    if (value instanceof String) {
      String v = (String) value;
      if (type.equals ("number")) {
	try {
	  value = new Reusable.RFloat (Float.parseFloat (v));
	} catch (NumberFormatException nfe) {
	  value = new Reusable.RFloat (0.0f);
	  System.out.println ("SchObject.addField - <" + name + ">" +
			      " type " + type +
			      " value <" + v +
			      "> had a bad format.");
	}
      }
      else if (type.equals ("boolean"))
        value = v.equals ("true") ? Operator.TRUE : Operator.FALSE;
      else if (type.equals ("datetime")) {
	try {
	  value = new Reusable.RInteger (timeOps.stringToTime (v));
	} catch (NumberFormatException nfe) {
	  value = new Reusable.RInteger (0);
	  System.out.println ("SchObject.addField - <" + name + ">" +
			      " type " + type +
			      " value " + value +
			      " : value not in number format.");
	}
      }
//      else if (! type.equals ("string")) {
//	System.out.println ("SchObject.addField - <" + name + ">" +
//			    " type " + type +
//			    " value " + value +
//			    " uses an unknown type.");
//      }
      if (iskey)
        key = v;
    }
    if (isListField) {
      List elementList = (List) data.get (name);
      elementList.add (value);
      if (debug)
	System.out.println ("SchObject.addField - adding " + name + 
			    "->" + value + 
			    " (" + elementList.size () + 
			    " total)");
    }
    else
      data.put (name, value);
  }

  public void addListField (String name) {
    if (debug)
      System.out.println ("SchObject.addListField - adding " + name);

    data.put (name, new ArrayList());
  }

  public Object getField (String name) {
    return data.get (name);
  }

  public String getKey() {
    return key;
  }

  public String toString () {
    for (Iterator iter = data.keySet ().iterator (); iter.hasNext (); ) {
      Object key = iter.next (); 
      return "" + key  + "=" + data.get(key);
    }
    return "SchObject";
  }
}
