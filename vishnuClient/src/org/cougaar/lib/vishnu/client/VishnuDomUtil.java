/*
 * <copyright>
 *  
 *  Copyright 2001-2004 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 * 
 *  You can redistribute this software and/or modify it under the
 *  terms of the Cougaar Open Source License as published on the
 *  Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * </copyright>
 */

package org.cougaar.lib.vishnu.client;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Date;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.cougaar.util.ConfigFinder;
import org.cougaar.util.log.Logger;

import org.cougaar.lib.param.ParamMap;

/** A collection of helper methods for manipulating DOM documents. */
public class VishnuDomUtil {
  public VishnuDomUtil (ParamMap myParamTable, String name, ConfigFinder configFinder, Logger logger) {
    this.myParamTable = myParamTable;
    this.name = name;
    this.configFinder = configFinder;
    this.logger = logger;
  }
  
  protected ParamMap getMyParams () {	return myParamTable;  }

  /** sets showTiming parameter */
  protected void localSetup () 
  {
    try {showTiming = getMyParams().getBooleanParam("showTiming");}    
    catch(Exception e) {showTiming = false;}
  }
  
  /** 
   * Makes a human-readable String representation of a dom document <p>
   *
   * Uses XMLSerializer to do its work.
   * 
   * @see org.apache.xml.serialize.XMLSerializer
   * @param doc the document to translate into a String
   * @return String readable document equivalent
   */
  protected String getDocAsString (Document doc) {
    StringWriter sw = new StringWriter();

    OutputFormat of = new OutputFormat (doc, OutputFormat.Defaults.Encoding, true);
    of.setLineWidth (150);
    XMLSerializer serializer = new XMLSerializer (sw, of);
    try {
      Date start = new Date();
      serializer.serialize (doc);
      if (showTiming)
	reportTime (" - got doc as string in ", start);
    } catch (IOException ioe) {logger.error ("Exception " + ioe, ioe);}
    
    return sw.toString ();
  }
  
  /** write document to a CharArrayWriter stream using XMLSerializer */
  protected CharArrayWriter getDocAsArray (Document doc) {
    CharArrayWriter sw = new CharArrayWriter();

    OutputFormat of = new OutputFormat (doc);
    of.setPreserveSpace (false);
	
    XMLSerializer serializer = new XMLSerializer (sw, of);
    try {
      serializer.serialize (doc);
    } catch (IOException ioe) {logger.error ("Exception " + ioe, ioe);}

    return sw;
  }

  /**
   * appends the document at filename onto originalDoc.
   *
   * @param originalDoc - doc to append the filename doc to
   * @param filename    - name of the document to append
   */
  protected void appendDoc (Document originalDoc, String filename) {
    Element originalRoot = originalDoc.getDocumentElement ();
    appendDoc (originalDoc, originalRoot, filename);
  }

  /**
   * appends the document at filename onto originalDoc.
   *
   * @param originalDoc - doc to append the filename doc to
   * @param filename    - name of the document to append
   */
  protected void appendDoc (Document originalDoc, Element originalAppendLoc,
			    String filename) {
    try {
      DOMParser parser = new DOMParser ();
      InputStream inputStream = configFinder.open(filename);
      parser.parse (new InputSource(inputStream));
      Document appendDoc = parser.getDocument ();

      Element appendDocRoot = appendDoc.getDocumentElement ();
      merge (originalAppendLoc, appendDocRoot);

    } catch (SAXException sax) {
      logger.error (name + ".appendDoc - Got sax exception:\n" + sax, sax);
    } catch (IOException ioe) {
      logger.error ("Could not open file : \n" + ioe, ioe);
    }
  }

  /**
   * appends the document at filename onto originalDoc.
   *
   * @param originalDoc - doc to append the filename doc to
   * @param filename    - name of the document to append
   */
  protected void appendChildrenToDoc (Document originalDoc, Element originalAppendLoc,
				      String filename) {
    try {
      DOMParser parser = new DOMParser ();
      InputStream inputStream = configFinder.open(filename);
      parser.parse (new InputSource(inputStream));
      Document appendDoc = parser.getDocument ();

      Element appendDocRoot = appendDoc.getDocumentElement ();
	  
      if (appendDocRoot.getTagName().equals ("GLOBAL_DATA_LIST")) {
		
	NodeList nlist = appendDocRoot.getChildNodes();

	for (int i = 0; i < nlist.getLength(); i++) {
	  Node rootChild = nlist.item (i);
	  merge (originalAppendLoc, rootChild);
	}
      }
      else
	merge (originalAppendLoc, appendDoc.getDocumentElement ());
	  
    } catch (SAXException sax) {
      logger.error (name + ".appendDoc - Got sax exception:\n" + sax, sax);
    } catch (IOException ioe) {
      logger.error ("Could not open file : \n" + ioe, ioe);
    }
  }

  /**
   * Takes two nodes of xml documents and makes the rootToAdd
   * node as a child of the placeToAdd node.  
   *
   * On the first call, these two nodes are the roots of two
   * separate xml documents.
   *
   * It recurses down the tree to be added.
   *
   * Note that naively taking the root to be added and
   * adding it directly by doing appendChild doesn't work,
   * since all DOM Nodes have an "owner document," and all
   * nodes in a tree must have the same owner.  Since the
   * rootToAdd node comes from a different document, you'll get
   * a "Wrong Document Err" when you try to do this.
   * 
   * So you have to create copies with the target document's
   * createElement method and add those.
   *
   * BOZO : probably better way to do this!
   * @param placeToAdd - the root of the destination document
   * @param rootToAdd  - the root of the document to merge into the first
   *                     doc
   * 
   */
  protected void merge (Node placeToAdd, Node rootToAdd) {
    Document targetDoc = placeToAdd.getOwnerDocument ();

    // clone the node to be added

    if (rootToAdd.getNodeType() == Node.ELEMENT_NODE) {
      Node clonedNode = createClone (rootToAdd, targetDoc);

      placeToAdd.appendChild (clonedNode);

      NodeList nlist = rootToAdd.getChildNodes();
      int nlength = nlist.getLength();

      for(int i = 0; i < nlength; i++){
	Node child = nlist.item(i);
	merge (clonedNode, child);
      }
    }
    else if (rootToAdd.getNodeType() == Node.TEXT_NODE) {
      String data = rootToAdd.getNodeValue ().trim();
      if (data.length () > 0) {
	Text textNode = targetDoc.createTextNode (data);
	placeToAdd.appendChild (textNode);
      }
    }
  }

  /**
   * Clone a node (only its attributes)
   *
   * @param toClone - the node to copy
   * @param doc     - is the factory for new nodes
   * @return the clone
   */
  protected Node createClone (Node toClone, Document doc) {
    Element clonedNode = doc.createElement (toClone.getNodeName ());

    // clone the attributes
    NamedNodeMap attrs = toClone.getAttributes ();
    for(int i = 0; i < attrs.getLength (); i++) {
      Attr attrNode = (Attr) attrs.item (i);
      clonedNode.setAttribute (attrNode.getName (), attrNode.getValue ());
    }
    
    return clonedNode;
  }

  /** write document to an output stream using XMLSerializer */
  protected void writeDocToStream (Document doc, OutputStream os) {
    OutputFormat of = new OutputFormat (doc, "UTF-8", true);
    of.setLineWidth (150);
	
    XMLSerializer serializer = new XMLSerializer (os, of);

    try {
      serializer.serialize (doc);
    } catch (IOException ioe) {logger.error ("Exception " + ioe, ioe);}
  }
  
  /** 
   * Prints out time since <code>start</code> with prefix <code>prefix</code>
   * @param start since when
   * @param prefix meaning of time difference
   */
  public void reportTime (String prefix, Date start) 
  {
    Runtime rt = Runtime.getRuntime ();
    Date end = new Date ();
    long diff = end.getTime () - start.getTime ();
    long min  = diff/60000l;
    long sec  = (diff - (min*60000l))/1000l;
    long millis = diff - (min*60000l) - (sec*1000l);
    //	if (min < 1l && sec < 1l && millis < 10l) return;
    String msg = 
      name + prefix +
      min + 
      ":" + ((sec < 10) ? "0":"") + sec + 
      ":" + ((millis < 10) ? "00" : ((millis < 100) ? "0":"")) + millis + 
      " (Wall clock)" + 
      " free "  + (rt.freeMemory  ()/(1024*1024)) + "M" +
      " total " + (rt.totalMemory ()/(1024*1024)) + "M";

    logger.info (msg);

    System.out.println (msg);
  }

  protected ParamMap myParamTable;

  boolean showTiming;
  String name;
  ConfigFinder configFinder;
  protected Logger logger;
}
