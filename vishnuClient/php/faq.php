<?
// This software is to be used in accordance with the COUGAAR license
// agreement. The license agreement and other information can be found at
// http://www.cougaar.org.
//
// Copyright 2001 BBNT Solutions LLC
//
//
// Dynamic generation of FAQ.
// Note that to get the non-dynamic version, save the web page as
// faq.html, replace all references to faq.php and fulldoc.php
// with faq.html and fulldoc.html, and remove the reference to "Home".

  $qs = array();
  function addq ($question, $answer, $name="", $putbrs=1) {
    global $qs;
    $qs[] = array ($question, $answer, $name, $putbrs);
  }

  addq ("What is Vishnu?",
        "Vishnu is an optimizing reconfigurable scheduler plus a web-based
        system for defining the problems for the scheduler to solve
        and viewing the results of the scheduler.");
  addq ("What is scheduling?",
        "Scheduling is deciding how to allocate one or more scarce
        resources.  These resources are allocated in order to accomplish
        particular tasks.  Usually, but not always, the resources are
        allocated to the tasks for limited intervals of time.
        (Scheduling problems that do not consider time are often instead
        referred to as assignment problems.)");
  addq ("What is optimized scheduling?",
        "In most well-defined scheduling problems, there are hard
        constraints (i.e., constraints that absolutely must be satisfied)
        and soft constraints (i.e., constraints that can be violated but
        at a cost).  If the problem is even moderately difficult, there will
        be no way to adhere to all the soft constraints, and therefore
        tradeoffs will have to be made.  Combining the costs incurred
        by all the different violations of soft constraints gives a
        measure of how good a particular schedule is.  Optimized scheduling
        searches the space of all possible legal schedules for one that
        comes as close as possible to being the optimal with respect to
        this cost function.");
  addq ("What is a reconfigurable scheduler, and what is so good about it?",
        "Most schedulers solve a limited class of scheduling problems
        in a single domain.
        A reconfigurable scheduler greatly widens the the range of problems
        that the scheduler can solve and can work in any domain.
        For example, a reconfigurable scheduler can just as easily
        schedule taxi cab pickups as soccer fields, just as easily schedule
        classrooms for test as crews to fly airplanes, just as easily
        schedule jobs in a flexible factory as calls for repairpeople.
        The flexibility to tailor a reconfigurable scheduler to your
        problem of interest is what makes it so powerful.");
  addq ("Why is it named Vishnu?",
        "The Hindu god Vishnu is the preserver.
        He acts whenever the good and evil in the world become unbalanced
        in order to restore the balance.  Like Vishnu, our scheduler
        considers many different factors and attempts to optimally balance
        the tradeoffs between them.");
  addq ("How did Vishnu get started?",
        "Vishnu was developed as part of the DARPA's Advanced Logistics
        Program (ALP).  The ALP program manager, Todd Carrico, challenged
        us to develop a generic (i.e., reconfigurable) scheduler for use
        with <a href=\"http://www.cougaar.org\">Cougaar</a>,
        an infrastructure developed under ALP for building
        multiagent systems.  The goal was that a non-programmer should
        be able to set up a <a href=\"http://www.cougaar.org\">Cougaar</a>
        agent to do scheduling.  We designed
        Vishnu to work either as a standalone scheduler or as part of
        <a href=\"http://www.cougaar.org\">Cougaar</a>.",
        "origin");
  addq ("What are the components of Vishnu?",
        "There are three components of Vishnu: the web server, the
        automated scheduler and the formula compiler.  The web server,
        backed by a relational database, is at the center of it all.
        It provides the data exchange mechanism between all the other
        pieces (automated scheduler, formula compiler, human users, and
        external clients), as well as the data for the web browsers that
        provide the graphical interface.  The automated scheduler does
        the optimized scheduling.  The formula compiler translates from
        easily readable formulas to a form usable by the scheduler.
        A more in-depth description and an architecture diagram are
        provided in <a href=\"fulldoc.php#arch\">Section 2</a> of the
        full documentation.");
  addq ("How do I install Vishnu and get it running?",
        "<a href=\"fulldoc.php#d\">Appendix D</a> of the full documentation
        provides a full description of the installation and setup of
        Vishnu.  In brief, the steps are as follows:
        <ol>
        <li> Install the web server.  This involves installing compatible
        versions of Apache, MySQL, PHP, and GD.
        The easiest way to install the Apache/MySQL/PHP/GD
        combination is to use AbriaSQL Standard from
        <a href=\"http://www.abriasoft.com\">Abriasoft</a>, or
        alternatively NuSphere MySQL from
        <a href=\"http://www.nusphere.com\">NuSphere</a>.
        This will do the whole install automatically.
        If you prefer to do it yourself, there are instructions on how
        to install the Apache/MySQL/PHP/GD combination on a UNIX
        machine in the documentation.
        <li> Install Java and an XML Java library on the machines
        where the scheduler(s) and compiler(s) will be running.  (This
        can be the same machine as the web server.
        <li> Install the Vishnu code.
        </ol>",
        "", 0);
  addq ("Do I need my own personal web server to run Vishnu?",
        "No, Vishnu is designed to share a web sharer with other
        applications and among the different Vishnu problems.
        The intent of the design of Vishnu is that a particular site
        should require only a single Vishnu server and that this server
        should be able to handle many different problems.");
  addq ("I now have Vishnu installed.  How do I get started?",
        "The best way to get started with Vishnu is to start playing with
        the sample problems.  In the directory testdata, there are a
        variety of files which can be loaded into the web server using
        the Upload function on the home page.  You can recognize the
        loadable files by their <i>vsh</i> extension.  Try running the
        scheduler on them and looking at the results.  Then try changing
        the scheduling specifications and rerunning the scheduler
        and see how the results change.  Don't worry about messing
        up the problems; you can always reload them again from file.
        When you understand the sample problems, then try making a problem
        of your own.  <a href=\"fulldoc.php#gui\">Section
        6</a> of the full documentation is good to read if you prefer
        to start by reading rather than by playing.");
  addq ("I am now ready to define a problem of my own.  How do I do it?",
        "There are three ways to define a problem.
        The first is to use a web browser.
        How to do this is described in <a href=\"fulldoc.php#gui\">Section
        6</a> of the full documentation.  A second approach is to load
        a problem definition from a file.  How to do this loading is
        described in <a href=\"fulldoc.php#gui\">Section
        6</a> of the full documentation.  You can create such a file
        either by <a href=\"faq.php#saving\">saving a problem that you
        have created</a> or by creating it from scratch using the
        format described in <a href=\"fulldoc.php#b\">Appendix B</a>
        of the full documentation.  A third way to define a problem is
        to have an external client feed the problem definition to the
        web server.  You either need to write your own external client
        or else utilize the <a href=\"faq.php#bridge\">Cougaar-Vishnu
        bridge</a>");
  addq ("How do I save a problem to file for future use or for loading
        on another server?",
        "On the problem page, there is a "Save to File" link.  If you click
        here, you will be given four different options of what to save.
        If you select \"Problem definition and data\", everything
        (metadata, scheduling logic, and data) gets saved.
        This is also described in <a href=\"fulldoc.php#gui\">Section
        6</a> of the full documentation.",
        "saving");
  addq ("Can I run Vishnu without a web server?",
        "Yes, you can run just the automated scheduler portion of Vishnu
        independent of the web server.
        There is an internal mode which
        allows you to invoke the scheduler directly from a Java
        application.  However, there are three important caveats.
        First, without the web server you will be unable to graphically
        view the results.  Second,
        this currently requires you to have
        compiled (XML) versions of the scheduling logic formulas, which means
        that you should have first set up the problem using the full
        web-based system (since the compiler does not run without the
        web server).  Third, because you must invoke the scheduler
        from a Java application, you must either
        have the expertise to write such an application or else
        use the <a href=\"faq.php#bridge\">Cougaar-Vishnu bridge</a>.");
  addq ("Can I run more than one automated scheduler with a single
        web server?",
        "Yes, multiple schedulers can accept problems from a single web
        server.  Having multiple schedulers is particularly useful when
        they are running on different computers or CPUs, so that you
        get the benefits of parallel execution.");
  addq ("Why must there be a single object type for tasks and a single
        object type for resources?",
        "The formula compiler must know what type of object each task
        and resource is going to be in order to be able to do its
        consistency checks and ensure that the formulas make sense.");
  addq ("What happens if I have resources (or tasks) of different
        object types?",
        "There are three different options, and the right solution
        depends on the problem.  One possible solution is to divide the
        problem into independent separate problems, with each
        subproblem handling one type of resource.  Another possible
        solution is to divide the problem into interconnected subproblems,
        with the connection implemented using the
        <a href=\"http://www.cougaar.org\">Cougaar</a> multiagent
        infrastructure and the <a href=\"faq.php#bridge\">Cougaar-Vishnu
        bridge</a>.  A third solution is to define a single
        \"superobject\" that contains the data fields from the different
        object types plus a field that tells which type of underlying
        object each superobject actually is.");
  addq ("How does problem-specific scheduling logic work?",
        "The formulas of the scheduling logic
        are evaluated by the scheduler at different
        points during its execution.  For example, to determine the list
        of all resources that can perform a given task, the scheduler
        will execute the Capability Criterion formula with <i>task</i>
        set to the given task and <i>resource</i> set to each of the
        resources in succession.  The results that the scheduler finds
        will therefore depend on how you have defined these formulas.");
  addq ("How do I go about developing a good set of formulas for the
        scheduling logic for my problem?",
        "The development of the problem-specific scheduling logic
        is an iterative
        process.  In general, you should not expect to get them correct
        the first time.  You should have one or more test problems
        available to evaluate the schedule produced by a particular set
        of specifications.  Start with your first attempt at the
        specifications.  Run with the current formulas on the test problem(s),
        analyze the results, determine what needs to be improved, and
        edit the formulas to accomplish this.  Iterate through this process
        until you get good results.  Save the formulas to file when
        you have ones you like.");
  addq ("What are the genetic algorithm parameters, and how do I know
        how to choose them?",
        "The description of what each paramter does is given in
        <a href=\"fulldoc.php#gaparameters\">this table</a> in the
        full documentation.  The best choices for these values vary
        greatly depending on the problem.  Sometimes there is any easy
        answer.  For example, if there is no contention for resources
        (i.e., no two tasks would prefer to be assigned to the same
        resource at overlapping time intervals), then the best solution
        has a maximum evaluations of 1, hence taking the guaranteed
        optimal schedule generated by the first individual.  However,
        often it is not obvious what the best values for the parameters
        are, and some experimentation may be required.");
  addq ("How can I optimize the performance of the Vishnu scheduler?",
        "There are two different measures of performance: (i) speed
        and (ii) quality of the schedule.   There is generally a tradeoff
        between these two measures; by running the scheduler longer the
        genetic algorithm can find better solutions.  So, one way
        to optimize performance is to make sure that the
        genetic algorithm parameters are set to make the optimal tradeoff
        with respect to your needs.<P>A second way to optimize performance
        is to make the formulas in the scheduling specifications as simple
        as possible.  The setup and wrapup duration specifications get
        executed particularly frequently and so should be the object of
        particular attention.<P>A third way to optimize performance is
        to reduce the amount of data being fed to the scheduler.  This
        can be accomplished by eliminating any unused data fields and
        by using global pointers for any large blocks of data that are
        repeated across many tasks and/or resources.<P>A fourth approach
        is to use the scheduler in internal mode, hence avoiding the
        data transfer overhead involved in going through the web server.
        The down side of this is that you will not be able to see the
        results using the web server.");
  addq ("How does Vishnu handle dynamic rescheduling for problems that
        are continually changing?",
        "The basic approach is that the user/client must drive a loop
        that continually loads new data into Vishnu and asks the
        scheduler to derive a new schedule. There are a few features
        of Vishnu that facilitate the process of dynamic
        rescheduling.  First, there is the ability of Vishnu to handle data
        updates.  This allows the problem not to have to be loaded from
        scratch each time one piece of data gets updated.  Second,
        there is the capability to specify that certain assignments
        be frozen.  This allows the user to specify that certain past
        assignments should not be overridden despite future changes to
        the data.  Third, there is the scheduling window.  This allows
        the scheduler to slide its view of the data as time progresses
        and to accept as frozen assignments in the past.  A fourth
        feature that would be useful (but is not yet implemented)
        is the ability for a user to
        specify soft constraints involving schedule stability.  This
        would allow the scheduler to change previously made assignments
        while still expressing a preference for keeping the assignments
        as they were.");
  addq ("Why does Vishnu not support [fill in your favorite feature]?",
        "Vishnu is still in its early stages of development.  There are
        a variety of features that are on our \"to do\" list.  However,
        please do provide suggestions about what you feel would be
        particularly useful features for Vishnu to add.  Such suggestions
        at the very least can help us to prioritize and could provide
        new ideas that we have not thought of.");
  addq ("Why did you choose Apache/MySQL/PHP for the web server and
        Java for the scheduler?",
        "The criteria we used for selecting our development environment
        were: platform independence, speed, power, acceptance level, and
        free availability.  The Apache/MySQL/PHP combination provides
        a platform-independent solution that is very fast and provides
        the power we need to do some complex server-side scripting.
        The combination is available for free and has reached a high level
        of acceptance, so much so that Red Hat is distributing it
        bundled with its Linux 7.0 release.  PHP was not appropriate for
        the scheduler (partly because the scheduler does not necessarily
        run on the same machine as the web server),
        so we used Java due to its platform independence
        and the ease of development that Java provides.");
  addq ("Why did you not use an existing language, such as AMPL, for
        defining the scheduling problems?",
        "First, our approach is a much better fit to the way our
        automated scheduler works.  AMPL, and other similar languages,
        are oriented towards mathematical programming problems, where
        constraints are expressed as equations and inequalities in
        some variables.  There are constraints expressible as equations
        and inequalities that our scheduler cannot handle, and likewise,
        constraints that our scheduler allows that cannot be expressed
        using a language like AMPL.  While we could work on trying to
        extend AMPL, it made more sense to develop a framework for
        expressing constraints targeted to our scheduler.<P>
        A second reason is that the symbolic approach we have developed
        for expressing constraints is more intuitive to those not
        trained in mathematics and computer programming.  This is
        important if we hope to eventually reach a large user community.");
  addq ("What does the Cougaar-Vishnu bridge do?",
        "The bridge makes it very easy to define a scheduler to fit into
        a <a href=\"http://www.cougaar.org\">Cougaar</a> infrastructure.
        The bridge does the following things automatically.  It translates
        the object structure and object data from a Cougaar Logical Data
        Model (LDM) into equivalent Vishnu metadata and data.  It writes
        a scheduling problem to Vishnu, starts the scheduler, and reads
        back the assignments.  It then updates the allocations in the Cougaar
        LDM to reflect the assignments.  The main thing that it leaves to
        you, the user, is defining the scheduling specifications.",
        "bridge");
  addq ("How do I use a bridge plugin within a Cougaar society?",
        "This is described in <a href=\"fulldoc.php#bridge\">Section
        7</a> of the full documentation.");
  addq ("Who do I contact if I have any questions about Vishnu?",
        "Email any questions about Vishnu proper to
        <A HREF=\"mailto:dmontana@bbn.com\">David Montana</A>.  Email
        any questions about the Cougaar-Vishnu bridge to
        <A HREF=\"mailto:gvidaver@bbn.com\">Gordon Vidaver</A>.");

  require ("navigation.php");

  function getTitle () {
    echo "Vishnu FAQ";
  }

  function getHeader() {
    echo "Vishnu Frequently Asked Questions";
  }

  function mainContent () {
    global $qs;
    echo "<DIV align=left><OL>\n"; 
    for ($i = 0; $i < sizeof ($qs); $i++) {
      $ref = $qs[$i][2] ? $qs[$i][2] : ("q" . ($i + 1));
      echo "<li><a href=\"faq.php#" . $ref . "\">" . $qs[$i][0] .
           "</a></li>\n";
    }
    echo "</OL>\n<BR>\n";
    for ($i = 0; $i < sizeof ($qs); $i++) {
      $ref = $qs[$i][2] ? $qs[$i][2] : ("q" . ($i + 1));
      echo "<a name=\"" . $ref . "\"><b>" . $qs[$i][0] .
           "</b></a></br>\n" . $qs[$i][1] . ($qs[$i][3] ? "<br><br>\n" : "\n");
    }
    echo "</DIV>\n";
  }
?>

