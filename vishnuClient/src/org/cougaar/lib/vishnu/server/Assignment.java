// $Header: /opt/rep/cougaar/vishnu/vishnuClient/src/org/cougaar/lib/vishnu/server/Attic/Assignment.java,v 1.2 2001-04-06 18:50:31 dmontana Exp $

package org.cougaar.lib.vishnu.server;

/**
 * Assignment of a single task to a single resource
 *
 * <copyright>
 *  Copyright 2000-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR license agreement.
 * </copyright>
 */

public class Assignment extends TimeBlock {

  private Task task;
  private Resource resource;
  private int taskEndTime;
  private int taskStartTime;
  private boolean frozen = false;

  public Assignment (Task task, Resource resource, int startTime,
                     int taskStartTime, int taskEndTime, int endTime,
                     boolean frozen, TimeOps timeOps) {
    super (startTime, endTime, timeOps);
    this.task = task;
    this.resource = resource;
    this.taskStartTime = taskStartTime;
    this.taskEndTime = taskEndTime;
    this.frozen = frozen;
  }

  public Task getTask() { return task; }
  public Resource getResource() { return resource; }
  public int getTaskStartTime() { return taskStartTime; }
  public int getTaskEndTime() { return taskEndTime; }
  public boolean getFrozen() { return frozen; }

  public String toString() {
    return ("<ASSIGNMENT " + attributesString() + " />");
  }

  public String attributesString() {
    return ("task=\"" + task.getKey()
            + "\" resource=\"" + resource.getKey()
            + "\" taskstart=\"" + timeOps.timeToString (getTaskStartTime())
            + "\" taskend=\"" + timeOps.timeToString (getTaskEndTime())
            + "\" frozen=\"" + (frozen ? "yes" : "no")
            + "\" " + super.attributesString());
  }
}
