package org.cougaar.lib.vishnu.client;

import org.cougaar.domain.planning.ldm.asset.Asset;

import org.cougaar.domain.planning.ldm.plan.Aggregation;
import org.cougaar.domain.planning.ldm.plan.AspectScorePoint;
import org.cougaar.domain.planning.ldm.plan.AspectType;
import org.cougaar.domain.planning.ldm.plan.AspectValue;
import org.cougaar.domain.planning.ldm.plan.MPTask;
import org.cougaar.domain.planning.ldm.plan.PlanElement;
import org.cougaar.domain.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.domain.planning.ldm.plan.Preference;
import org.cougaar.domain.planning.ldm.plan.ScoringFunction;
import org.cougaar.domain.planning.ldm.plan.Task;
import org.cougaar.domain.planning.ldm.plan.Verb;

import org.cougaar.domain.glm.ldm.Constants;

import org.cougaar.lib.callback.UTILAggregationCallback;
import org.cougaar.lib.callback.UTILFilterCallback;
import org.cougaar.lib.callback.UTILGenericListener;
import org.cougaar.lib.callback.UTILWorkflowCallback;

import org.cougaar.lib.filter.UTILAggregatorPlugIn;

import org.cougaar.lib.util.UTILAllocate;
import org.cougaar.lib.util.UTILAggregate;
import org.cougaar.lib.util.UTILExpand;
import org.cougaar.lib.util.UTILPlugInException;
import org.cougaar.lib.util.UTILPreference;
import org.cougaar.lib.util.UTILPrepPhrase;

import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.AttributeList;

import org.cougaar.util.StringKey;

/**
 * Aggregator version of ALP-Vishnu Bridge.
 *                                                                          <p>
 * The key thing to note is that the asset in the task->asset               <br> 
 * association is on the WITH preposition of the generated MPTask.
 *                                                                          <p>
 * This is critical, because the allocator downstream will look for this    <br>
 * prep and pluck the asset off to make the allocation.                      
 *                                                                          <p>
 * Uses the assigned start and end times to set the preferences and         <br>
 * allocation results.
 *                                                                          <p>
 * Adjust preferences so that the start time preference is the assigned     <br>
 * start time, and the end time preference has a best date that is the      <br>
 * assigned end time.  The early and late dates of the end time preference  <br>
 * are the same as the first parent task. (This isn't very important, as the<br>
 * downstream allocator should just allocate to the start and best times.)
 *                                                                          <p>
 * Many times subclasses will want to redefine                              <br>
 * <code>interestingTask</code> and <code>interestingAsset</code> to filter <br>
 * what goes to the Vishnu scheduler.  If you do, it's good form to call    <br>
 * super.interestingTask().
 *                                                                          <p>
 * @see org.cougaar.lib.vishnu.client.VishnuPlugIn#interestingAsset
 * <!--
 * (When printed, any longer line will wrap...)
 *345678901234567890123456789012345678901234567890123456789012345678901234567890
 *       1         2         3         4         5         6         7         8
 * -->
 */
public class VishnuAggregatorPlugIn extends VishnuPlugIn implements UTILAggregatorPlugIn {

  /** adds the aggregation filter */
  public void setupFilters () {
    super.setupFilters ();

    if (myExtraOutput)
      System.out.println (getName () + " : Filtering for Aggregations...");

    addFilter (myAggCallback    = createAggCallback    ());
  }


  /*** Callback for input tasks ***/
  protected UTILWorkflowCallback   myWorkflowCallback;
  /** Callback for input tasks ***/
  protected UTILFilterCallback createThreadCallback (UTILGenericListener bufferingThread) { 
    if (myExtraOutput)
      System.out.println (getName () + " Filtering for tasks with Workflows...");

    myWorkflowCallback = new UTILWorkflowCallback  (bufferingThread); 
    return myWorkflowCallback;
  } 
  /** Callback for input tasks ***/
  protected UTILFilterCallback getWorkflowCallback () {
    return myWorkflowCallback;
  }
  
  /*** Callback for Aggregations ***/
  /** Callback for Aggregations **/
  protected UTILAggregationCallback   myAggCallback;
  /** Callback for Aggregations **/
  protected UTILAggregationCallback getAggCallback    () { return myAggCallback; }
  /** Callback for Aggregations **/
  protected UTILAggregationCallback createAggCallback () { 
    return new UTILAggregationCallback  (this); 
  } 


  /*** Stuff for AggregationListener ***/

  /**
   * Should almost always call interestingTask. Can't here because one isn't defined yet.
   **/
  public boolean interestingParentTask (Task t) { return interestingTask(t); }
  /** implemented for AggregationListener */
  public boolean needToRescind (Aggregation agg) { return false; }
  /** implemented for AggregationListener */
  public void reportChangedAggregation(Aggregation agg) { updateAllocationResult (agg); }
  /** implemented for AggregationListener */
  public void handleSuccessfulAggregation(Aggregation agg) {
	if (agg.getEstimatedResult().getConfidenceRating() > UTILAllocate.MEDIUM_CONFIDENCE)
	  handleRemovedAggregation (agg);
	else if (myExtraOutput) {
      System.out.println (getName () + 
						  ".handleSuccessfulAggregation : got changed agg (" + 
						  agg.getUID () + 
						  ") with intermediate confidence."); 
	}
  }

  /** implemented for AggregationListener */
  public void handleRemovedAggregation (Aggregation agg) {	
	sendUpdatedRoleSchedule(null, 
							getAssetFromMPTask (agg.getComposition().getCombinedTask ()), 
							agg.getComposition().getParentTasks ());
  }

  /** implemented for AggregationListener */
  public boolean handleRescindedAggregation(Aggregation agg) { return false; }

  /** implemented for AggregationListener */
  public void publishRemovalOfAggregation (Aggregation aggToRemove) {
    Task changedTask = aggToRemove.getTask ();
    publishRemove (aggToRemove);
    publishChange (changedTask);
  }
  

  public void handleIllFormedTask (Task t) { reportIllFormedTask(t); }

  /**
   * <pre>
   * Overrides VishnuPlugIn.parseAnswer
   *
   * Uses a SAX parser to buffer up task->asset assignments.  These
   * come directly from a Vishnu URL.
   *
   * This is necessary, since we need to make parent tasks->MPTask
   * mappings in makePlanElements.
   *
   * Calls makePlanElements, readXML
   * </pre>
   * @see #makePlanElements
   * @see org.cougaar.lib.vishnu.client.VishnuPlugIn#readXML
   * @see org.cougaar.lib.vishnu.client.VishnuPlugIn#parseAnswer
   */
  protected void parseAnswer() {
    if (myExtraOutput)
      System.out.println ("VishnuPlugIn.waitTillFinished - Vishnu scheduler result returned!");
    try {
	  int unhandledTasks = myTaskUIDtoObject.size ();

	  comm.getAnswer (new AssignmentHandler ());

	  if (myExtraOutput || true)
		System.out.println (getName () + ".parseAnswer - created successful plan elements for " +
							(unhandledTasks-myTaskUIDtoObject.size ()) + " tasks.");

      handleImpossibleTasks (myTaskUIDtoObject.values ());
      myTaskUIDtoObject.clear ();
    } catch (Exception e) {
      System.out.println ("BAD URL : " + e);
	  e.printStackTrace ();
    }
  }

  private static final SimpleDateFormat format =
    new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

  protected Asset assignedAsset;
  protected Date start, end, setup, wrapup;
  protected Vector alpTasks = new Vector ();

  /**
   * BOZO - must do something with start and end.
   */
  protected void parseStartElement (String name, AttributeList atts) {
	try {
	  if (myExtraExtraOutput || debugParseAnswer)
		System.out.println ("parseStartElement got " + name);
	  
	  if (name.equals ("MULTITASK")) {
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
	  makePlanElement (alpTasks, assignedAsset, start, end, setup, wrapup);
	  alpTasks.clear ();
	}
	else if (name.equals ("TASK")) {}
	else if (debugParseAnswer) {
	  System.out.println (getName () + ".parseEndElement - ignoring tag " + name);
	}
  }

  /**
   * make plan elements given a list of assignments.
   * @param taskList - tasks for this asset
   * @param asset that these tasks are grouped for
   * @param start time start
   * @param end time end
   */
  public void makePlanElement (Vector tasklist, Asset anAsset, Date start, Date end, Date setupStart, Date wrapupEnd) {
	if (myExtraOutput) UTILAggregate.setDebug (true);

	List aggResults = UTILAggregate.makeAggregation(this,
													ldmf,
													realityPlan,
													tasklist,
													getVerbForAgg(tasklist),
													getPrepPhrasesForAgg(anAsset, tasklist),
													getDirectObjectsForAgg(tasklist),
													getPreferencesForAgg(anAsset, tasklist, start, end),
													getClusterIdentifier(),
													getAspectValuesMap(tasklist, start, end),
													UTILAllocate.MEDIUM_CONFIDENCE);
	if (myExtraOutput) UTILAggregate.setDebug (false);

	publishList(aggResults);
      
	cleanupAggregation(anAsset, tasklist, aggResults);

	Task mpTask = findMPTask (aggResults);

	if (makeSetupAndWrapupTasks && 
		((setupStart.getTime() < start.getTime()) ||
		 (wrapupEnd.getTime () > end.getTime  ())))
	  makeSetupWrapupExpansion (mpTask, anAsset, start, end, setupStart, wrapupEnd);
  }

  /** 
   * hook for after publish processing                          <br>
   * Default does nothing.                                      <br>
   * might want to do :	Task mpTask = findMPTask (aggResults);
   */
  protected void cleanupAggregation(Asset a, List tasklist, List aggResults) {
	//	Task mpTask = findMPTask (aggResults);
  }

  /** 
   * Find MPTask in list returned from UTILAggregate.makeAggregation
   *
   * @see org.cougaar.lib.callback.UTILAggregate#makeAggregation
   */
  protected MPTask findMPTask(List results) {
    Iterator i = results.iterator();
    while (i.hasNext()) {
      Object next = i.next();
      if (next instanceof MPTask)
		return ((MPTask)next);
    }
    throw new UTILPlugInException(myClusterName + " couldn't find MPTask in list of Aggregation products");
  }

  /** 
   * Defines the verb of the MPTask
   * Transport by default
   * 
   * @return Verb - Transport
   */
  protected Verb getVerbForAgg(List g) { 
    return Constants.Verb.Transport; 
  }

  /** 
   * Aggregates direct objects of parent tasks into a vector
   * 
   * @return aggregate of parent direct objects
   */
  protected Vector getDirectObjectsForAgg(List parentTasks) {
    Vector assets = new Vector();

    Iterator pt_i = parentTasks.iterator();
    // prepPhrases and directObjects
    while (pt_i.hasNext()) {
      Task currentTask = (Task)pt_i.next();
      assets.addElement(currentTask.getDirectObject());
    }

    return assets;
  }

  /**
   * Adjust preferences so that the start time preference is the assigned 
   * start time, and the end time preference has a best date that is the 
   * assigned end time.  The early and late dates of the end time preference
   * are the same as the first parent task. (This isn't very important, as the
   * downstream allocator should just allocate to the start and best times.)  <p>
   * 
   * If there is a quantity preference on the first task, will calculate an aggregate <br>
   * quantity preference.  Assumes then that all tasks will have a quantity pref.
   * @param a - the asset associated with the MPTask
   * @param g - parent task list
   * @param start - the date for the START_TIME preference
   * @param end - the best date for the END_TIME preference
   * @return Vector - list of preferences for the MPTask
   */
  protected Vector getPreferencesForAgg(Asset a, List g, Date start, Date end) { 
	Task firstParentTask = (Task)g.get(0);

	Date earlyDate = start.after (UTILPreference.getEarlyDate(firstParentTask)) ?
	  start : UTILPreference.getEarlyDate(firstParentTask);
	
    Vector prefs = UTILAllocate.enumToVector(firstParentTask.getPreferences());
	prefs = UTILPreference.replacePreference (prefs, UTILPreference.makeStartDatePreference (ldmf, start));
	prefs = UTILPreference.replacePreference (prefs, 
											  UTILPreference.makeEndDatePreference (ldmf, 
																					earlyDate,
																					end,
																					UTILPreference.getLateDate(firstParentTask)));
	long totalQuantity = 0l;
	if (UTILPreference.hasPrefWithAspectType(firstParentTask, AspectType.QUANTITY)) {
	  for (Iterator iter = g.iterator (); iter.hasNext (); )
		totalQuantity += UTILPreference.getQuantity ((Task) iter.next());

	  prefs = 
		UTILPreference.replacePreference (prefs, UTILPreference.makeQuantityPreference (ldmf, totalQuantity));
	}

	return prefs;
  }

  /**
   * <pre>
   * Defines how to get the asset representing the task->asset association.
   *
   * Should be in sync with getPrepPhrasesForAgg, which attaches the prep.
   * </pre>
   * @see #getPrepPhrasesForAgg
   * @param combinedTask - the MPTask generated by the plugin
   * @return the asset on the task
   */
  protected Asset getAssetFromMPTask (MPTask combinedTask) {
	return (Asset) UTILPrepPhrase.getIndirectObject(combinedTask, Constants.Preposition.WITH);
  }

  /**
   * <pre>
   * Defines how the MPTask holds the asset for the task->asset association.
   *
   * Should be in sync with getAssetFromMPTask, which accesses the prep.
   *
   * Critical, because the allocator downstream will look for this prep and
   * pluck the asset off to make the allocation.
   *
   * </pre>
   * @see #getAssetFromMPTask
   * @param a - asset to attach to task
   * @param g - parent tasks
   * @return the original set of prep phrases from the first parent task PLUS the WITH
   *         prep with the asset
   */
  protected Vector getPrepPhrasesForAgg(Asset a, List g) {
    Vector preps = UTILAllocate.enumToVector(((Task)g.get(0)).getPrepositionalPhrases());
    preps.addElement(UTILPrepPhrase.makePrepositionalPhrase(ldmf, 
															Constants.Preposition.WITH, 
															a));
	return preps;
  }

  /**
   * <pre>
   * Create aspect values so that the start time aspect is the assigned 
   * start time, and the end time aspect is the 
   * assigned end time.
   * (The downstream allocator should just echo these values.)
   *
   * Gets the preferences and makes aspect values that echo them. At this
   * point the start and end time preferences have been set to the assigned 
   * times.
   * 
   * </pre>
   * @param a - the asset associated with the MPTask
   * @param g - parent task list
   * @return AspectValue[] - returned aspect values
   */
  protected AspectValue [] getAVsForAgg(Asset a, List g, Date start, Date end) {
    return makeAVsFromPrefs(getPreferencesForAgg(a, g, start, end));
  }

  /**
   * <pre>
   * Create aspect values so that the start time aspect is the assigned 
   * start time, and the end time aspect is the 
   * assigned end time.
   * (The downstream allocator should just echo these values.)
   *
   * Gets the preferences and makes aspect values that echo them. At this
   * point the start and end time preferences have been set to the assigned 
   * times.
   * 
   * </pre>
   * @param a - the asset associated with the MPTask
   * @param g - parent task list
   * @return AspectValue[] - returned aspect values
   */
  protected Map getAspectValuesMap(List g, Date start, Date end) {
	Map taskToAspectValue = new HashMap ();
	for (Iterator iter = g.iterator (); iter.hasNext (); ) {
	  Task task = (Task) iter.next ();
	  taskToAspectValue.put (task, 
							 makeAVsFromPrefs(UTILAllocate.enumToVector(task.getPreferences ())));
	}
	return taskToAspectValue;
  }

  /**
   * <pre>
   * Create aspect values so that the start time aspect is the assigned 
   * start time, and the end time aspect is the 
   * assigned end time.
   * (The downstream allocator should just echo these values.)
   *
   * Takes the preferences and makes aspect values that echo them. At this
   * point the start and end time preferences have been set to the assigned 
   * times.
   * 
   * </pre>
   * @param prefs - the preferences associated with the MPTask
   * @return AspectValue[] - returned aspect values
   */
  protected AspectValue [] makeAVsFromPrefs(Vector prefs) {
    Vector tmp_av_vec = new Vector(prefs.size());
    Iterator pref_i = prefs.iterator();
    while (pref_i.hasNext()) {
      // do something really simple for now.
      Preference pref = (Preference) pref_i.next();
      int at = pref.getAspectType();

      ScoringFunction sf = pref.getScoringFunction();
      // allocate as if you can do it at the "Best" point
      double result = ((AspectScorePoint)sf.getBest()).getValue();

	  // sometimes would fail task due to rounding error 
	  // (time would appear a few millis before the START_TIME pref)
      if (at == AspectType.START_TIME)
		result += 1000.0d; // BOZO : hack -- still needed?

      tmp_av_vec.addElement(new AspectValue(at, result));

	  //System.out.println (getName() + ".makeAVsFromPrefs - adding type " + at + " value " + result);
    }      

    AspectValue [] avs = new AspectValue[tmp_av_vec.size()];
    Iterator av_i = tmp_av_vec.iterator();
    int i = 0;
    while (av_i.hasNext())
      avs[i++] = (AspectValue)av_i.next();

    // if there were no preferences...return an empty vector (0 elements)
    return avs;
  }

  /**
   * <pre>
   * publish the generated plan elements, compositions, and tasks
   * 
   * Also informs of failed tasks.
   *
   * <pre>
   * @param toPublish stuff to publish
   */
  protected void publishList(List toPublish) {
    Iterator i = toPublish.iterator();
    while (i.hasNext()) {
      Object next_o = i.next();
	  if (next_o instanceof Task) {
		Task taskToPublish = (Task) next_o;
		PlanElement pe = taskToPublish.getPlanElement ();
		if (pe != null && !pe.getEstimatedResult().isSuccess () && myExtraOutput) {
		  System.out.println (getName () + 
							  ".publishList - Task " + taskToPublish.getUID () +
							  " failed : ");
		  UTILExpand.showPlanElement (taskToPublish);
		}
	  }
      boolean success = publishAdd(next_o);
    }
  }

  boolean debugParseAnswer = false;
}

