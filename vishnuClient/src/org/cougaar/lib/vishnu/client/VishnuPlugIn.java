package org.cougaar.lib.vishnu.client;

import org.cougaar.domain.glm.ldm.Constants;

import org.cougaar.lib.callback.UTILAssetCallback;
import org.cougaar.lib.callback.UTILAssetListener;
import org.cougaar.lib.filter.UTILBufferingPlugInAdapter;
import org.cougaar.lib.param.ParamException;
import org.cougaar.lib.util.UTILAllocate;
import org.cougaar.lib.util.UTILExpand;
import org.cougaar.lib.util.UTILPreference;
import org.cougaar.lib.util.UTILPrepPhrase;
import org.cougaar.lib.util.UTILRuntimeException;

import org.cougaar.domain.planning.ldm.asset.Asset;

import org.cougaar.domain.planning.ldm.plan.AllocationResultAggregator;
import org.cougaar.domain.planning.ldm.plan.Expansion;
import org.cougaar.domain.planning.ldm.plan.NewTask;
import org.cougaar.domain.planning.ldm.plan.PlanElement;
import org.cougaar.domain.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.domain.planning.ldm.plan.Role;
import org.cougaar.domain.planning.ldm.plan.Task;
import org.cougaar.domain.planning.ldm.plan.Workflow;

import org.cougaar.core.society.UniqueObject;

import org.cougaar.util.StringKey;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.util.TimeSpan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.SAXParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.cougaar.lib.vishnu.server.Scheduler;
import org.cougaar.lib.vishnu.client.VishnuComm;
import org.cougaar.lib.vishnu.client.VishnuDomUtil;

/**
 * <pre>
 * ALP-Vishnu bridge.
 *
 * Base class for interacting with the Vishnu scheduler.
 *
 * Abstract because it does not define :
 *  - createThreadCallback
 * each of which is defined in the allocator, aggregator, and expander 
 * subclasses.
 *
 * </pre>
 * <!--
 * (When printed, any longer line will wrap...)
 *345678901234567890123456789012345678901234567890123456789012345678901234567890
 *       1         2         3         4         5         6         7         8
 * -->
 */
public abstract class VishnuPlugIn 
  extends UTILBufferingPlugInAdapter 
  implements UTILAssetListener {

  private static final SimpleDateFormat format =
    new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

  /**
   * Here all the various runtime parameters get set.  See documentation for details.
   */
  public void localSetup() {     
    super.localSetup();

    try {hostName = getMyParams().getStringParam("hostName");}    
    catch(Exception e) {hostName = "dante.bbn.com";}

    try {testing = getMyParams().getBooleanParam("testing");}    
    catch(Exception e) {testing = false;}

    try {showFormatXML = getMyParams().getBooleanParam("showFormatXML");}    
    catch(Exception e) {showFormatXML = false;}

	//    try {ignoreSpecsFile = getMyParams().getBooleanParam("ignoreSpecsFile");}  
	//    catch(Exception e) {ignoreSpecsFile = false;}

	//    try {sendSpecsEveryTime = 
	//		   getMyParams().getBooleanParam("sendSpecsEveryTime");}    
	//    catch(Exception e) {sendSpecsEveryTime = false;}

    try {alwaysClearDatabase = 
		   getMyParams().getBooleanParam("alwaysClearDatabase");}    
    catch(Exception e) {alwaysClearDatabase = true;}

    try {showTiming = 
		   getMyParams().getBooleanParam("showTiming");}    
    catch(Exception e) {showTiming = true;}

    try {sendRoleScheduleUpdates = 
		   getMyParams().getBooleanParam("sendRoleScheduleUpdates");}    
    catch(Exception e) {sendRoleScheduleUpdates = false;}

    try {makeSetupAndWrapupTasks = 
		   getMyParams().getBooleanParam("makeSetupAndWrapupTasks");}    
    catch(Exception e) {makeSetupAndWrapupTasks = true;}

    try {runInternal = 
		   getMyParams().getBooleanParam("runInternal");}    
    catch(Exception e) {runInternal = false;}

	// writes the XML sent to Vishnu web server to a file (human readable)
	/*
    try {writeXMLToFile = 
		   getMyParams().getBooleanParam("writeXMLToFile");}    
    catch(Exception e) {writeXMLToFile = false;}
	*/

    // how many of the input tasks to use as templates when producing the 
	// OBJECT FORMAT for tasks
    try {firstTemplateTasks = getMyParams().getIntParam("firstTemplateTasks");}    
    catch(Exception e) {firstTemplateTasks = 2;}

    try {sendDataChunkSize = getMyParams().getIntParam("sendDataChunkSize");}    
    catch(Exception e) {sendDataChunkSize = 100;}
	
	domUtil = new VishnuDomUtil (getMyParams(), getName(), getConfigFinder());
	comm    = new VishnuComm    (getMyParams(), getName(), getClusterName(), domUtil, runInternal);
	xmlProcessor = new XMLProcessor (getMyParams(), getName(), getClusterName(), domUtil, comm, getConfigFinder());
	config  = new VishnuConfig  (getMyParams(), getName(), getClusterName());
	
	// helpful for debugging connection configuration problems
	if (runInternal)
	  System.out.println (getName () + " - will run internal Vishnu Scheduler.");
	else
	  System.out.println (getName () + " - will try to connect to Vishnu Web Server : " + 
						  hostName);
  }

  /****************************************************************
   ** Setup Filters...
   **/

  public void setupFilters () {
    super.setupFilters ();

    if (myExtraOutput)
      System.out.println (getName () + " : Filtering for generic Assets...");

    addFilter (myAssetCallback    = createAssetCallback    ());
  }

  /**
   * Is the task interesting to the plugin?  This is the inner-most part of <br> 
   * the predicate.                                                         <br>
   * By default, it ignores tasks produced from this plugin                 <br>                    
   * If you redefine this, it's good to call this using super.
   *
   * @param t - the task begin checked
   */
  public boolean interestingTask (Task t) { 
	PrepositionalPhrase pp = UTILPrepPhrase.getPrepNamed (t, "VISHNU"); 
	if (pp != null && ((String) pp.getIndirectObject()).equals (getClassName(this)))
	  return false;
	return true;
  }

  public void handleIllFormedTask (Task t) {}

  protected UTILAssetCallback getAssetCallback    () { return myAssetCallback; }

  /**
   * Standard Asset callback
   *
   * @see org.cougaar.lib.callback.UTILPhysicalAssetCallback
   * @see org.cougaar.lib.callback.UTILNotOrganizationCallback
   */
  protected UTILAssetCallback createAssetCallback () { 
    return new UTILAssetCallback  (this); 
  } 

  /**
   * <pre>
   * Implemented for UTILAssetListener
   *
   * OVERRIDE to see which assets you
   * think are interesting
   * </pre>
   * @param a asset to check for notification
   * @return boolean true if asset is interesting
   */
  public boolean interestingAsset(Asset a) {
    return true;
  }

  /**
   * <pre>
   * Place to handle new assets.
   *
   * Does nothing by default - reports new assets when myExtraOutput set.
   *
   * </pre>
   * @param newAssets new assets found in the container
   */
  public void handleNewAssets(Enumeration newAssets) {
	if (myExtraOutput) {
	  Set changed = new HashSet ();
	  for (; newAssets.hasMoreElements (); ){
		Object asset = newAssets.nextElement ();
		changed.add (asset);
	  }
	  System.out.println (getName () + ".handleNewAssets - got new assets = " + changed);
	}
  }

  /**
   * <pre>
   * Place to handle changed assets.
   *
   * Does nothing by default - reports changed assets when myExtraOutput set.
   *
   * </pre>
   * @param newAssets changed assets found in the container
   */
  public void handleChangedAssets(Enumeration changedAssets) {
	if (myExtraOutput)
	  System.out.println (getName () + ".handleChangedAssets - got changed assets.");
  }
  
  /**
   * <pre>
   * To be called from handleSuccessfulXXX methods in allocator, aggregator, 
   * and expander.
   *
   * It's up to them to take apart plan element to send task->asset mappings.
   * 
   * Inefficient for large numbers of tasks that could go in one batch to the 
   * web server.
   *
   * If sendRoleScheduleUpdates is false, this method is ignored.
   *
   * </pre>
   * @param changedAsset -- the asset that has been allocated to, with an 
   *        updated role schedule
   * @param assignedTask -- the task that was frozen, now should be unfrozen
   */
  /*
  protected void sendUpdatedRoleSchedule(PlanElement pe, Asset changedAsset, 
										 Collection assignedTasks) {
	if (!sendRoleScheduleUpdates)
	  return;
	
	if (myExtraOutput) {
	  System.out.println (getName () + 
						  ".sendUpdatedRoleSchedule - got changed asset = " + 
						  changedAsset);
	}
	
	Set frozenTasks = new HashSet (myFrozenTasks);
	frozenTasks.retainAll (assignedTasks);

	if (frozenTasks.isEmpty ()) 
	  return;
	
	Document doc = new DocumentImpl ();
    Element newRoot = doc.createElement("PROBLEM");
	newRoot.setAttribute ("NAME", comm.getProblem());
    doc.appendChild(newRoot);
	
	for (Iterator iter = frozenTasks.iterator (); iter.hasNext (); ) {
	  UniqueObject task = (UniqueObject) iter.next ();
	  Element unfreeze = doc.createElement("UNFREEZE");
	  unfreeze.setAttribute ("TASK", task.getUID ().getUID());
	  newRoot.appendChild (unfreeze);
	  if (myExtraOutput)
		System.out.println (getName () + 
							".sendUpdatedRoleSchedule - unfreezing " + 
							task.getUID ());
	}
	
	if (myExtraOutput)
	  System.out.println (getName () + 
						  ".sendUpdatedRoleSchedule - sending " + 
						  frozenTasks.size () + 
						  " changed assets.");

	serializeAndPostData (doc);

	if (myExtraOutput)
	  System.out.println (getName () + 
						  ".sendUpdatedRoleSchedule - sending updated asset " +
						  changedAsset.getUID ());

	List changed = new ArrayList ();
	changed.add (changedAsset);
    sendDataToVishnu (changed, myNameToDescrip, 
					  false, // don't clear database
					  true, // send assets as CHANGEDOBJECTS
					  singleAssetClassName);

	myFrozenTasks.removeAll (frozenTasks);
  }
*/

  /**
   * <pre>
   * Deal with the tasks that we have accumulated.
   * 
   * Does, in order: 
   * 1) Sends the problem's object format, if it hasn't already been sent.
   * 2) Records tasks so can unfreeze them later.
   * 3) Sends the data (obtained from tasks and assets)
   * 4) Starts the scheduler
   * 5) Waits for a result
   * 
   * </pre>
   *
   * @param tasks the tasks to handle
   */
  public void processTasks (List tasks) {
	System.out.println (getName () + ".processTasks - received " + 
						tasks.size () + " tasks");
	if (runInternal)
	  internalBuffer.append ("<root>");

	Date start = new Date();
      
	prepareObjectFormat (tasks);
      
	setUIDToObjectMap (tasks, myTaskUIDtoObject);

	if (myExtraOutput)
	  System.out.println (getName () + ".processTasks - sending " + 
						  myTaskUIDtoObject.values ().size () + " tasks.");
      
	int numTasks = myTaskUIDtoObject.values ().size ();
	Date dataStart = new Date();
      
	prepareData (tasks);
	  
	if (showTiming) {
	  domUtil.reportTime (" - Vishnu completed data XML processing in ", dataStart);
	  domUtil.reportTime (" - Vishnu completed XML processing in ", start);
	}

	waitForAnswer ();
	  
	if (showTiming)
	  domUtil.reportTime (" - Vishnu did " + numTasks + " tasks in ", start);
  }

  protected void prepareObjectFormat (List tasks) {
	Date start = new Date();

	if (!sentFormatAlready) {
	  List assetClassName = new ArrayList(); // just a way of returning a second return value from function
	  Collection formatTemplates = config.getAssetTemplatesForTasks(tasks, assetClassName, getAllAssets());
	  singleAssetClassName = (String) assetClassName.get(0);
		
	  formatTemplates.addAll (config.getTemplateTasks(tasks, firstTemplateTasks));

	  if (myExtraOutput) {
		System.out.println (getName () + ".processTasks - " + formatTemplates.size() + " unique assets : ");
		for (Iterator iter = formatTemplates.iterator (); iter.hasNext(); )
		  System.out.print ("\t" + iter.next().getClass ());
		System.out.println ("");
	  }

	  myNameToDescrip = sendFormat (formatTemplates, singleAssetClassName);

	  if (!runInternal)
		sentFormatAlready = true;
	  if (showTiming)
		domUtil.reportTime (" - Vishnu completed format XML processing in ", start);
	}
  }
  
  /**
   *
   */
  protected void prepareData (List stuffToSend) {
	Collection allAssets = getAllAssets();
	if (myExtraOutput)
	  System.out.println (getName () + ".processTasks - sending " + 
						  allAssets.size () + " assets.");
	  
	stuffToSend.addAll (allAssets);
	  
	if (myExtraExtraOutput) 
	  for (Iterator iter = stuffToSend.iterator (); iter.hasNext (); ) {
		Object obj = iter.next ();
		  
		System.out.println (getName () + ".processTasks sending stuff " + 
							((UniqueObject) obj).getUID ());
	  }
	  
	setUIDToObjectMap (allAssets, myAssetUIDtoObject);
	  
	sendDataToVishnu (stuffToSend, myNameToDescrip, 
					  alwaysClearDatabase, 
					  false, // send assets as NEWOBJECTS
					  singleAssetClassName);
  }
  
  protected void waitForAnswer () {
	if (runInternal) {
	  runInternally ();
	} else {
	  comm.startScheduling ();
	
	  if (!waitTillFinished ())
		showTimedOutMessage ();
	}
  }
  
  private void showTimedOutMessage () {
	System.out.println (getName () + ".processTasks -- ERROR -- " + 
						"Timed out waiting for scheduler to finish.\n" +
						"Is there a scheduler running?\n" + 
						"See vishnu/scripts/runScheduler in the vishnu distribution.\n" +
						"It's good to set the machines property to include only\n" + 
						"those machines you are running from, or else the scheduler\n" +
						"could process any job posted by anyone to the web server.\n" +
						"For more information, contact gvidaver@bbn.com or dmontana@bbn.com");
  }

  /** 
   * Run internally.  Create a new scheduler, and give it the contents of <p>
   * the internalBuffer, which has captured all the xml output that would <p>
   * normally go to the various URLs.  Then, parse the results using a SAX <p>
   * Parser and the AssignmentHandler, which just calls parseStartElement and <p>
   * parseEndElement.  The AssignmentHandler will create plan elements for
   * each assignment.
   *
   * @see #parseStartElement
   * @see #parseEndElement
   */
  protected void runInternally () {
	Scheduler internal = new Scheduler ();
	internalBuffer.append ("</root>");
	if (myExtraExtraOutput)
	  System.out.println(getName () + ".runInternally - sending stuff " + internalBuffer.toString());

	int unhandledTasks = myTaskUIDtoObject.size ();

	String assignments = internal.runInternalToProcess (internalBuffer.toString());
	if (myExtraOutput)
	  System.out.println(getName () + ".runInternally - scheduled assignments were : " + assignments);
	
	SAXParser parser = new SAXParser();
	//	parser.setDocumentHandler (new AssignmentHandler ());
	parser.setContentHandler (new AssignmentHandler ());
	try {
	  parser.parse (new InputSource (new StringReader (assignments)));
	} catch (SAXException sax) {
	  System.out.println (getName () + ".runInternally - Got sax exception:\n" + sax);
	} catch (IOException ioe) {
	  System.out.println (getName () + ".runInternally - Could not open file : \n" + ioe);
	} catch (NullPointerException npe) {
	  System.out.println (getName () + ".runInternally - ERROR - no assignments were made, badly confused : \n" + npe);
	}
	clearInternalBuffer ();

	if (myExtraOutput)
	  System.out.println (getName () + ".runInternally - created successful plan elements for " +
						  (unhandledTasks-myTaskUIDtoObject.size ()) + " tasks.");

	handleImpossibleTasks (myTaskUIDtoObject.values ());
	myTaskUIDtoObject.clear ();
  }
  
  protected void setUIDToObjectMap (Collection objects, Map UIDtoObject) {
	for (Iterator iter = objects.iterator (); iter.hasNext ();) {
	  UniqueObject obj = (UniqueObject) iter.next ();
	  StringKey key = new StringKey (obj.getUID().getUID());
	  if (!UIDtoObject.containsKey (key)) {
		UIDtoObject.put (key, obj);
	  }
	}
  }

  protected Collection getAllAssets() {
	return getAssetCallback().getSubscription ().getCollection();
  }

  /** 
   * Utility method for finding all assets. 
   * @return Iterator that iterates over assets.
   */
  protected final Iterator getAssets() {
    Collection assets = getAllAssets();

    if (assets.size() != 0) {
      return assets.iterator();
    }
    return new ArrayList ().iterator();
  }

  /**
   * <pre>
   * send the dataformat section of the problem to the postdata
   * URL.
   *
   * This will define the structure of input tasks and resources,
   * as well as the scheduling specs and the ga parameters.
   *
   * Each of the items in the template collection will be translated
   * into an xml OBJECTFORMAT tag.
   *
   * </pre>
   * @param templates -- a collection of all the template resources 
   *                     and a template task.
   * @return map of the object types to their object descriptions
   */
  protected Map sendFormat (Collection templates, String assetClassName) {
	if (myExtraOutput)
	  System.out.println (getName () + ".sendFormat, resource " + assetClassName);
    Map nameInfo = null;
	Date start = new Date ();
	
	List returnedMap = new ArrayList();
	  
	Document problemFormatDoc = 
	  xmlProcessor.getFormatDocWithoutDuplicates (templates, assetClassName, returnedMap);
	nameInfo = (Map) returnedMap.get (0);

	if (showTiming)
	  domUtil.reportTime (" - Vishnu completed format XML transform in ", start);

      // append any global other data object formats 
	appendGlobalDataFormat (problemFormatDoc);

	// append the scheduling specs
	String specsFile = config.getSpecsFile();

	if (myExtraOutput)
	  System.out.println (getName () + ".sendFormat - appending " + 
						  specsFile + " vishnu specs xml file");

	domUtil.appendDoc (problemFormatDoc, specsFile);

      // append the ga specs
	specsFile = config.getGASpecsFile(); 

	if (myExtraOutput)
	  System.out.println (getName () + ".sendFormat - appending " + 
						  specsFile + " vishnu ga specs xml file");

	domUtil.appendDoc (problemFormatDoc, specsFile);

      // send to postdata URL
	comm.serializeAndPostProblem (problemFormatDoc, runInternal, internalBuffer);

    return nameInfo;
  }

  protected void appendGlobalDataFormat (Document problemFormatDoc) {
	String otherDataFormat = config.getOtherDataFormat();
	try {
	  if (getConfigFinder ().open (otherDataFormat) != null) {
		if (myExtraOutput)
		  System.out.println (getName () + ".sendFormat -  appending " + 
							  otherDataFormat + " other data format file");

		Node root = problemFormatDoc.getDocumentElement ();
		domUtil.appendDoc (problemFormatDoc, 
						   (Element)((Element)root).getFirstChild(), // OBJECTFORMAT tag
						   otherDataFormat);
	  }
	} catch (FileNotFoundException fnf) {
	  if (myExtraOutput)
		System.out.println (getName () + 
							".sendFormat could not find optional file : " + 
							otherDataFormat );
    } catch (Exception ioe) {
      System.out.println (getName() + ".sendFormat - Exception " + ioe.getMessage());
      ioe.printStackTrace ();
    }
  }
  
  /**
   * Send the data section of the problem to the postdata URL.<p>
   *
   * Chunks data into <code>sendDataChunkSize</code> chunks of tasks.<p>
   *
   * Handles sending changed objects.
   *
   * </pre>
   * @param tasks -- a collection of all the tasks and resources 
   * @param nameToDescrip - Maping of names to newnames on fields, objects
   * @param clearDatabase - send clear database command to Vishnu
   * @param sendingChangedObjects -- controls whether assets will be sent
   *                                 inside of <CHANGEDOBJECT> tags
   */
  protected void sendDataToVishnu (List tasks, 
								   Map nameToDescrip, 
								   boolean clearDatabase, 
								   boolean sendingChangedObjects,
								   String assetClassName) {
	int totalTasks = tasks.size ();
	int totalSent  = 0;

	DataXMLize dataXMLizer = xmlProcessor.getDataXMLizer (nameToDescrip, assetClassName);
	boolean sentOtherData = false;
	
	while (totalSent < totalTasks) {
	  int toIndex = totalSent+sendDataChunkSize;
	  if (toIndex > totalTasks)
		toIndex = totalTasks;
	  
	  Collection chunk = new ArrayList (tasks.subList (totalSent, toIndex));

	  if (myExtraOutput)
		System.out.println (getName () + ".sendDataToVishnu, from " + totalSent + " to " + toIndex);
	  
	  Document docToSend = 
		xmlProcessor.prepareDocument (chunk, dataXMLizer, clearDatabase, sendingChangedObjects, assetClassName,
									  sentOtherData, config.getOtherData());
	  sentOtherData = true;

	  comm.serializeAndPostData (docToSend, runInternal, internalBuffer);

	  if (clearDatabase) clearDatabase = false; // flip bit after first one
	  totalSent += sendDataChunkSize;
	}
  }

  /** wait until the scheduler is done.  Parse the answer if there was one. */
  public boolean waitTillFinished () {
	Date start = new Date();
	
	boolean gotAnswer = comm.waitTillFinished ();

	if (!alwaysClearDatabase) {
	  comm.serializeAndPostData (xmlProcessor.prepareFreezeAll (), runInternal, internalBuffer);
	}

	if (showTiming)
	  domUtil.reportTime (" - Vishnu received answer, was waiting for ", start);

    if (gotAnswer)
	  parseAnswer();

    return gotAnswer;
  }

  /**
   * <pre>
   * Reads XML from the URL to get the assignments.  Uses AssignmentHandler
   * (SAX) to parse XML.
   *
   * Uses myTaskUIDtoObject to figure out if there were any impossible tasks.
   * (If there were any, they will be in the myTaskUIDtoObject Map.)
   *
   * The AssignmentHandler removes any assigned tasks from myTaskUIDtoObject.
   *
   * </pre>
   */
  protected void parseAnswer() {
	if (myExtraOutput)
	  System.out.println (getName() + ".waitTillFinished - Vishnu scheduler result returned!");
	int unhandledTasks = myTaskUIDtoObject.size ();

	comm.getAnswer (new AssignmentHandler ());

	if (myExtraOutput)
	  System.out.println (getName () + ".parseAnswer - created successful plan elements for " +
						  (unhandledTasks-myTaskUIDtoObject.size ()) + " tasks.");

	handleImpossibleTasks (myTaskUIDtoObject.values ());
	myTaskUIDtoObject.clear ();
  }

  protected void clearInternalBuffer () {
	internalBuffer = new StringBuffer ();
  }

  /**
   * this is where we look up unique ids
   */
  public class AssignmentHandler extends DefaultHandler {
	/**
	 * just calls parseStartElement in plugin
	 */
	public void startElement (String uri, String local, String name, Attributes atts) throws SAXException {
	  parseStartElement (name, atts);
    }
	/**
	 * just calls parseEndElement in plugin
	 */
	public void endElement (String uri, String local, String name) throws SAXException {
	  parseEndElement (name);
    }
  }

  protected Asset assignedAsset;
  protected Date start, end, setup, wrapup;
  protected Vector alpTasks = new Vector ();

  /**
   * Given the XML that indicates assignments, parse it <p>
   *
   * Uses the <code>myTaskUIDtoObject</code> and <code>myAssetUIDtoObject</code> maps <br>
   * to lookup the keys returned in the xml to figure out which task was assigned to  <br>
   * which asset.  These were set in processTasks using setUIDToObjectMap.
   * 
   * @param name the tag name
   * @param atts the tag's attributes
   * @see #processTasks
   * @see #setUIDToObjectMap
   */
  protected void parseStartElement (String name, Attributes atts) {
	try {
	  if (myExtraExtraOutput || debugParseAnswer)
		System.out.println (getName() + ".parseStartElement got " + name);
	  
	  if (name.equals ("ASSIGNMENT")) {
		if (myExtraOutput) {
		  System.out.println (getName () + ".parseStartElement -\nAssignment: task = " + atts.getValue ("task") +
							  " resource = " + atts.getValue ("resource") +
							  " start = " + atts.getValue ("start") +
							  " end = " + atts.getValue ("end"));
		}
		String taskUID     = atts.getValue ("task");
		String resourceUID = atts.getValue ("resource");
		String startTime   = atts.getValue ("start");
		String endTime     = atts.getValue ("end");
		String setupTime   = atts.getValue ("setup");
		String wrapupTime  = atts.getValue ("wrapup");
		Date start         = format.parse (startTime);
		Date end           = format.parse (endTime);
		Date setup         = format.parse (setupTime);
		Date wrapup        = format.parse (wrapupTime);

		StringKey taskKey = new StringKey (taskUID);
		Task handledTask    = (Task)  myTaskUIDtoObject.get (taskKey);
		if (handledTask == null) {
		  System.out.println ("VishnuPlugIn - AssignmentHandler.startElement no task found with " + taskUID);
		  System.out.println ("\tmap was " + myTaskUIDtoObject);
		}
		else {
		  myTaskUIDtoObject.remove (taskKey);
		}

		Asset assignedAsset = (Asset) myAssetUIDtoObject.get (new StringKey (resourceUID));
		if (assignedAsset == null) 
		  System.out.println ("VishnuPlugIn - AssignmentHandler.startElement no asset found with " + resourceUID);
	
		myFrozenTasks.add (handledTask);
	  
		handleAssignment (handledTask, assignedAsset, start, end, setup, wrapup);
	  }
	  else if (name.equals ("MULTITASK")) {
		if (myExtraOutput || debugParseAnswer) {
		  System.out.println (getName () + ".parseStartElement -\nAssignment: " + 
							  " resource = " + atts.getValue ("resource") +
							  " start = " + atts.getValue ("start") +
							  " end = " + atts.getValue ("end") +
							  " setup = " + atts.getValue ("setup") +
							  " wrapup = " + atts.getValue ("wrapup"));
		}
		String taskList    = atts.getValue ("tasklist");
		String resourceUID = atts.getValue ("resource");
		String startTime   = atts.getValue ("start");
		String endTime     = atts.getValue ("end");
		String setupTime   = atts.getValue ("setup");
		String wrapupTime  = atts.getValue ("wrapup");
		start     = format.parse (startTime);
		end       = format.parse (endTime);
		setup     = format.parse (setupTime);
		wrapup    = format.parse (wrapupTime);

		assignedAsset = (Asset) myAssetUIDtoObject.get (new StringKey (resourceUID));
		if (assignedAsset == null) 
		  System.out.println (getName () + ".parseStartElement - no asset found with " + resourceUID);
	  }
	  else if (name.equals ("TASK")) {
		if (myExtraOutput || debugParseAnswer) {
		  System.out.println (getName () + ".parseStartElement -\nTask: " + 
							  " task = " + atts.getValue ("task"));
		}
		String taskUID = atts.getValue ("task");

		StringKey taskKey = new StringKey (taskUID);
		Task handledTask = (Task) myTaskUIDtoObject.get (taskKey);
		if (handledTask == null) 
		  System.out.println (getName () + ".parseStartElement - no task found with " + taskUID + 
							  " uid.");
		else
		  alpTasks.add (handledTask);

		// this is absolutely critical, otherwise VishnuPlugIn will make a failed disposition
		myTaskUIDtoObject.remove (taskKey);
		myFrozenTasks.add (handledTask);
	  }
	  else if (debugParseAnswer) {
		System.out.println (getName () + ".parseStartElement - ignoring tag " + name);
	  }
	} catch (NullPointerException npe) {
	  System.out.println (getName () + ".parseStartElement - got bogus assignment");
	  npe.printStackTrace ();
	} catch (ParseException pe) {
	  System.out.println (getName () + ".parseStartElement - start or end time is in bad format " + 
						  pe + "\ndates were : " +
						  " start = " + atts.getValue ("start") +
						  " end = " + atts.getValue ("end") +
						  " setup = " + atts.getValue ("setup") +
						  " wrapup = " + atts.getValue ("wrapup"));
	}
  }

  protected void parseEndElement (String name) {
	if (name.equals ("MULTITASK")) {
	  if (debugParseAnswer) {
		System.out.println (getName () + ".parseEndElement - got ending MULTITASK.");
	  }
	  for (int i = 0; i < alpTasks.size (); i++)
		handleAssignment ((Task) alpTasks.get(i), assignedAsset, start, end, setup, wrapup);
	  alpTasks.clear ();
	}
	else if (name.equals ("TASK")) {}
	else if (debugParseAnswer) {
	  System.out.println (getName () + ".parseEndElement - ignoring tag " + name);
	}
  }

  /**
   * Given a collection of impossible tasks, make failed dispositions for each.
   *
   * @param impossibleTasks -- tasks that the scheduler couldn't figure out 
   *                           what to do with
   */
  protected void handleImpossibleTasks (Collection impossibleTasks) {
	if (!impossibleTasks.isEmpty ())
	  System.out.println (getName () + 
						  ".handleImpossibleTasks - failing " + 
						  impossibleTasks.size () + 
						  " tasks.");
	
	for (Iterator iter = impossibleTasks.iterator (); iter.hasNext ();)
	    publishAdd (UTILAllocate.makeFailedDisposition (this, ldmf, 
							    (Task) iter.next ()));
  }

  /** 
   * define in subclass -- create an aggregation or allocation 
   *
   * The parameters are what got returned from the vishnu scheduler.
   * @param task  task that was assigned to asset
   * @param asset asset handling task
   * @param start of main task
   * @param end   of main task
   * @param setupStart start of setup task
   * @param wrapupEnd end of wrapup task
   */
  protected void handleAssignment (Task task, Asset asset, Date start, Date end, Date setupStart, Date wrapupEnd) {}

  /** must use a special allocation result aggregator that does NOT include the transit (setup, wrapup) tasks
   * in it's time calculations.
   */
  protected AllocationResultAggregator skipTransitARA = new VishnuAllocationResultAggregator ();

  /**
   * Make expansion of mptask that attached between one and three subtasks <p>
   *
   * May attach setup and wrapup tasks, if there are specs defined for them and <br>
   * if the <code>makeSetupAndWrapupTasks</code> boolean parameter is set. <p>
   * 
   * The parameters are what got returned from the vishnu scheduler.
   * @param task  task that was assigned to asset
   * @param asset asset handling task
   * @param start of main task
   * @param end   of main task
   * @param setupStart start of setup task
   * @param wrapupEnd end of wrapup task
   */
  protected List makeSetupWrapupExpansion (Task task, Asset asset, Date start, Date end, Date setupStart, Date wrapupEnd) {
    if (myExtraOutput)
	  System.out.println (getName () + ".makeSetupWrapupExpansion : " + 
			" assigning " + task.getUID() + 
			"\nto " + asset.getUID () +
			" from " + start + 
			" to " + end);
    boolean wantConfidence = false;
    
	// if true, the estimated alloc result has a medium confidence 
    try { wantConfidence = getMyParams().getBooleanParam ("wantMediumConfidenceOnExpansion"); }
    catch (Exception e) {}

	Vector subtasks = new Vector ();
	
	subtasks.add (createMainTask (task, asset, start, end, setupStart, wrapupEnd));

	if (makeSetupAndWrapupTasks) {
	  if (setupStart.getTime() < start.getTime()) {
		subtasks.add (createSetupTask (task, asset, start, end, setupStart, wrapupEnd));
		if (myExtraOutput)
		  System.out.println (getName () + ".makeSetupWrapupExpansion : making setup task for " + task.getUID());
	  }

	  if (wrapupEnd.getTime() > end.getTime()) {
		subtasks.add (createWrapupTask (task, asset, start, end, setupStart, wrapupEnd));
		if (myExtraOutput)
		  System.out.println (getName () + ".makeSetupWrapupExpansion : making wrapup task for " + task.getUID());
	  }
	}

    publishSubtasks (wantConfidence, task, subtasks);
	
	return subtasks;
  }

  /** 
   * <pre>
   * create first and possibly only subtask of MPTask
   * 
   * Time preferences have 
   *  1) the start time = earliest arrival = start of main task
   *  2) the best arrival time = latest arrival = end of main task
   *
   * Attaches WITH prep that shows which asset was used
   *
   * </pre>
   **/
  protected Task createMainTask (Task task, Asset asset, Date start, Date end, Date setupStart, Date wrapupEnd) {
	NewTask mainTask = (NewTask) UTILExpand.makeSubTask (ldmf, task, task.getDirectObject(), task.getSource());
	mainTask.setPrepositionalPhrases (getPrepPhrases (task, asset).elements());
	mainTask.setPreferences (getPreferences (task, start, start, end, end).elements());
	if (myExtraOutput) System.out.println (getName () + ".createMainTask : made main task : " + mainTask.getUID());
	return mainTask;
  }

  /** 
   * <pre>
   * create setup task that goes before main subtask
   * 
   * Time preferences have 
   *  1) the start time = earliest arrival = setup start
   *  2) the best arrival time = latest arrival = start of main task
   *
   * Attaches WITH prep that shows which asset was used
   *
   * </pre>
   **/
  protected Task createSetupTask (Task task, Asset asset, Date start, Date end, Date setupStart, Date wrapupEnd) {
	NewTask setupTask = (NewTask) UTILExpand.makeSubTask (ldmf, task, task.getDirectObject(), task.getSource());
	setupTask.setVerb (Constants.Verb.Transit);
	setupTask.setPrepositionalPhrases (getPrepPhrases (task, asset).elements());
	setupTask.setPreferences (getPreferences (task, setupStart, setupStart, start, start).elements());
	if (myExtraOutput) System.out.println (getName () + ".createSetupTask : made setup task : " + setupTask.getUID());
	return setupTask;
  }

  /** 
   * <pre>
   * create wrapup task that goes after main subtask
   * 
   * Time preferences have 
   *  1) the start time = earliest arrival = end of main task
   *  2) the best arrival time = latest arrival = end of wrapup
   *
   * Attaches WITH prep that shows which asset was used
   *
   * </pre>
   **/
  protected Task createWrapupTask (Task task, Asset asset, Date start, Date end, Date setupStart, Date wrapupEnd) {
	NewTask wrapupTask = (NewTask) UTILExpand.makeSubTask (ldmf, task, task.getDirectObject(), task.getSource());
	wrapupTask.setVerb (Constants.Verb.Transit);
	wrapupTask.setPrepositionalPhrases (getPrepPhrases (task, asset).elements());
	wrapupTask.setPreferences (getPreferences(task, end, end, wrapupEnd, wrapupEnd).elements());
	if (myExtraOutput) System.out.println (getName () + ".createWrapupTask : made wrapup task : " + wrapupTask.getUID());
	return wrapupTask;
  }

  /**
   * Adjust preferences so that the start time preference is the assigned 
   * start time, and the end time preference has a best date that is the 
   * assigned end time.  The early and late dates of the end time preference
   * are the same as the first parent task. (This isn't very important, as the
   * downstream allocator should just allocate to the start and best times.)
   * 
   * @param a - the asset associated with the MPTask
   * @param g - parent task list
   * @param start - the date for the START_TIME preference
   * @param end - the best date for the END_TIME preference
   * @return Vector - list of preferences for the MPTask
   */
  protected Vector getPreferences (Task parentTask, Date readyAt, Date earliest, Date best, Date latest) { 
    Vector prefs = UTILAllocate.enumToVector(parentTask.getPreferences());
	prefs = UTILPreference.replacePreference (prefs, UTILPreference.makeStartDatePreference (ldmf, readyAt));
	prefs = UTILPreference.replacePreference (prefs, 
											  UTILPreference.makeEndDatePreference (ldmf, 
																					earliest,
																					best,
																					latest));
	return prefs;
  }

  /** 
   * This method Expands the given Task.
   * @param t the task to be expanded.
   * @param subtasks the expanded subtasks
   */

  public void publishSubtasks (boolean wantConfidence, Task t, List subtasks) {
    if (myExtraOutput){
      System.out.println(getName() + ".handleTask: Subtask(s) created for task :" + 
						 t.getUID());
    }

    Workflow wf = UTILExpand.makeWorkflow (ldmf, subtasks, skipTransitARA, t);

    Expansion exp = null;
    if(wantConfidence){
      exp = UTILExpand.makeExpansionWithConfidence (ldmf, wf);
    }
    else{
      exp = UTILExpand.makeExpansion (ldmf, wf);
    }

    if (myExtraOutput){
      System.out.println(getName () + ".handleTask: Expansion created. (" +
			 exp.getUID() + ")");
    }
    
    for (Iterator i = subtasks.iterator (); i.hasNext ();) {
      publishAdd (i.next());
    }
    publishAdd(exp);

    if (myExtraOutput){
      System.out.println(getName() + ".handleTask: Expansion published. Workflow has " + 
						 UTILAllocate.enumToList(exp.getWorkflow ().getTasks()).size () + " subtasks." );
    }

  }

  /**
   * <pre>
   * Defines how the task holds the asset for the task->asset association.
   *
   * Critical, because the allocator downstream will probably look for this prep and
   * pluck the asset off to make the allocation or use it in some way.
   *
   * </pre>
   * @param a - asset to attach to task
   * @param g - parent tasks
   * @return the original set of prep phrases from the first parent task PLUS the WITH
   *         prep with the asset
   */
  protected Vector getPrepPhrases(Task parentTask, Asset a) {
    Vector preps = new Vector (UTILAllocate.enumToVector(parentTask.getPrepositionalPhrases()));
	if (!UTILPrepPhrase.hasPrepNamed (parentTask, Constants.Preposition.WITH))
	  preps.addElement(UTILPrepPhrase.makePrepositionalPhrase(ldmf, 
															  Constants.Preposition.WITH, 
															  a));

    preps.addElement(UTILPrepPhrase.makePrepositionalPhrase(ldmf, 
															"VISHNU", getClassName(this)));
	return preps;
  }

  protected StringBuffer internalBuffer = new StringBuffer ();
  protected Map myTypesToNodes, myNameToDescrip;

  protected UTILAssetCallback         myAssetCallback;
  protected int firstTemplateTasks;
  protected int sendDataChunkSize;
  protected boolean sentFormatAlready = false;

  protected boolean mySentAssetDataAlready = false;
  protected Map myTaskUIDtoObject = new HashMap ();
  protected Map myAssetUIDtoObject = new HashMap ();
  protected Set myFrozenTasks = new HashSet ();

  protected String hostName = "dante.bbn.com";

  protected boolean testing = false;
  protected boolean showFormatXML = false;

  protected boolean ignoreSpecsFile = false;
  protected boolean sendSpecsEveryTime = false;
  protected boolean alwaysClearDatabase = false;
  protected boolean showTiming = true;

  protected int numFilesWritten = 0; // how many files have been written out via the writeXMLToFile flag
  protected boolean sendRoleScheduleUpdates = false;
  protected boolean makeSetupAndWrapupTasks = true;
  protected boolean runInternal = true;

  private boolean debugParseAnswer = false;

  protected VishnuComm comm;
  protected VishnuDomUtil domUtil;
  protected String singleAssetClassName;
  XMLProcessor xmlProcessor;
  VishnuConfig config;
}
