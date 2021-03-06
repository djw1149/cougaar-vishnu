@ECHO OFF

REM "<copyright>"
REM " "
REM " Copyright 2001-2004 BBNT Solutions, LLC"
REM " under sponsorship of the Defense Advanced Research Projects"
REM " Agency (DARPA)."
REM ""
REM " You can redistribute this software and/or modify it under the"
REM " terms of the Cougaar Open Source License as published on the"
REM " Cougaar Open Source Website (www.cougaar.org)."
REM ""
REM " THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS"
REM " "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT"
REM " LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR"
REM " A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT"
REM " OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,"
REM " SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT"
REM " LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,"
REM " DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY"
REM " THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT"
REM " (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE"
REM " OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
REM " "
REM "</copyright>"


REM This script starts the Vishnu Scheduler
REM 
REM The Vishnu Scheduler polls the Vishnu web server looking for problems to schedule.
REM When it finds a request has been posted, it gets the problem and its scheduling specs
REM and produces a schedule.  This schedule is then posted back to the web server.

REM For values of properties 1-4, see com.bbn.vishnu.server.env.xml in this directory

REM CLASSPATH - the scheduler needs the com.bbn.vishnu.jar and the xerces jar in the CLASSPATH,
REM e.g. d:/vishnu/lib/vishnu.jar;d:/vishn/lib/xerces.jar 

REM PROPERTIES -
REM  1) host - Vishnu web server
REM  2) path - path on host to php files
REM  3) user - user on the web server (mysql user)
REM  4) password - password on the web server (mysql password)
REM  5) showAssignments - dump to stdout xml that shows task to resource assignments
REM  6) waitInterval    - scheduler poll period
REM  7) machines - (Very Important) list of machines where Vishnu clients are running.
REM     The scheduler will only try to do problems coming from these machines.
REM     If empty or undefined, scheduler will try to schedule all problems 
REM     posted to web server.  
REM
REM     Can be a comma separated list,
REM     e.g. -Dcom.bbn.vishnu.Scheduler.machines="pumpernickle, hammet"
REM
REM	  The scheduler will print which problems it's scheduling, so if you see problems 
REM     that aren't yours, you may want to restrict your scheduler.
REM
REM  8) problems - Specific problems this scheduler is restricted to handle.  For
REM  instance, if there were two problems running on machine X, and you had schedulers
REM  on machines Y and Z, and you wanted to make sure the scheduler on machine Y was only
REM  doing problem #1 and the scheduler on Z only do problem #2.  In this case, 
REM   on Y, you'd set -Dcom.bbn.vishnu.Scheduler.problems="problem_1" and
REM   on Z, you'd set -Dcom.bbn.vishnu.Scheduler.problems="problem_2"
REM
REM  Debugging params (safe to ignore):
REM
REM  9) Scheduler.debug - specific debugging of Scheduler class
REM  10) TimeBlock.debug - debugging of TimeBlock
REM  11) debug - general debug
REM  12) debugXML - show XML sent and received over URLs 
REM      (scheduler will not actually process requests)

set PROPERTIES=-Dcom.bbn.vishnu.host=localhost
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.path="/php/"
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.user=root
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.password=""
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.Scheduler.showAssignments=false
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.Scheduler.waitInterval=1000
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.Scheduler.machines=""
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.Scheduler.problems=""
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.Scheduler.debug=false
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.TimeBlock.debug=false
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.debug=false
set PROPERTIES=%PROPERTIES% -Dcom.bbn.vishnu.debugXML=false
set LIBPATHS= %VISHNU%\lib\xerces.jar;%VISHNU%\lib\vishnu.jar

java -classpath %LIBPATHS% %PROPERTIES% -Xms60m -Xmx100m com.bbn.vishnu.scheduling.Scheduler 


