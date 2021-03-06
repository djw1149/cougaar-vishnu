<?
// This software is to be used in accordance with the COUGAAR license
// agreement. The license agreement and other information can be found at
// http://www.cougaar.org.
//
// Copyright 2001 BBNT Solutions LLC
//
//
// Starts the scheduler by updating the tables of scheduling requests

  require ("browserlink.php");
  require ("utilities.php");
  require ("navigation.php");

  function getTitle () {
    global $problem;
    echo "Result of scheduler request";
  }

  function getHeader() {
    global $problem;
    echo "Problem <font color=\"green\">" . $problem . "</font>";
  }

  function getSubheader() { 
    global $problem, $user;
    $page = "<A HREF=\"status.php?problem=" . $problem .
            "\">scheduler status page</A>";
    $value = getschedulerstatus ($problem);
    if ($value && ($value["percent_complete"] != 100) &&
        ($value["percent_complete"] != -1)) {
      if ($value["requester"] == $user) {
        $str1 = "You are";
        $str2 = " and possibly cancel the current run";
      } else {
        $str1 = "User " . $value["requester"] . "is";
        $str2 = "";
      }
      echo $str1 . " already executing the scheduler " .
           "on this problem.<BR>Use the " . $page .
           " to view progress" . $str2 . ".";
    } else {
      $number = -1;
      if ($value)
        $number = deletecurrentrequest ($problem);
      $status = insertrequest ($problem, $user, $number + 1);
      if ($status) 
  	echo $status;
      else
          echo "Your scheduler request has been submitted.<br>You can " .
               "check its progress on the " . $page . ".";
    }

    mysql_close();
  }
?>
