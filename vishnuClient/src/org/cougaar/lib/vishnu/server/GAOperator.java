// $Header: /opt/rep/cougaar/vishnu/vishnuClient/src/org/cougaar/lib/vishnu/server/Attic/GAOperator.java,v 1.3 2001-04-12 17:50:30 dmontana Exp $

package org.cougaar.lib.vishnu.server;

/**
 * Interface for any class generating a child from one or more parents
 *
 * This software is to be used in accordance with the COUGAAR license
 * agreement. The license agreement and other information can be found at
 * http://www.cougaar.org.
 *
 * Copyright 2001 BBNT Solutions LLC
 */

public interface GAOperator {

  public int numParents();

  public Chromosome generateChild (Chromosome[] parents);

  public void setParms (String parms);

}
