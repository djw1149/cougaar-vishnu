<?
  require ("browserlink.php");
  require ("utilities.php");
  require ("navigation.php");

  function getTitle () {
    global $problem;
    echo "Tasks and Resources for " . $problem;
  }
  function getHeader () {
    global $problem;
    echo "Tasks and Resources for <font color=\"green\">" . $problem . "</font>";
  } 
  function getSubheader() { 
  }

  function mainContent () { 
    global $problem;

  $arr = gettaskandresourcetypes ($problem);
  $taskobject = $arr[0];
  $resourceobject = $arr[1];

  $result = mysql_db_query ("vishnu_prob_" . $problem,
               "select * from object_fields where is_key=\"true\";");
  while ($value = mysql_fetch_array ($result)) {
    if ($value["object_name"] == $taskobject)
      $taskkey = $value["field_name"];
    if ($value["object_name"] == $resourceobject)
      $resourcekey = $value["field_name"];
  }
  mysql_free_result ($result);
?>

<table border=0 CELLSPACING=0 CELLPADDING=0 COLS=3 WIDTH=100% >
<tr>
  <td width=13%>&nbsp;</td>
  <td align=left><font size=+1><b><u>Tasks</u></b></font></td>
  <td align=left><font size=+1><b><u>Resources</u></b></font></td>
</tr>
<tr><td colspan=3 ALIGN=CENTER>&nbsp;</td></tr>
<tr>
  <td width=1>&nbsp;</td>

  <td ALIGN=left valign=top>
<?
  $result = mysql_db_query ("vishnu_prob_" . $problem,
       		    "select obj_" . $taskkey . " from obj_" . $taskobject . " order by obj_" . $taskkey . ";");
  $i = 0;
  while ($value = mysql_fetch_row ($result)) {
      $url = "task.php?";
      $url .= "problem=" . $problem . "&";
      $url .= "taskobject=" . $taskobject . "&";
      $url .= "taskkey=" . $taskkey . "&";
      $url .= "resourceobject=" . $resourceobject . "&";
      $url .= "resourcekey=" . $resourcekey . "&";
      $url .= "taskname=" . urlencode($value[0]);
      echo $i++ . ") " . "<a href=\"" . $url . "\"/>" . $value[0] . "</a><br>";
  }
  mysql_free_result ($result);
?>
  </td>

  <td ALIGN=left valign=top>
<?
  $result = mysql_db_query ("vishnu_prob_" . $problem,
       		    "select obj_" . $resourcekey . " from obj_" . $resourceobject . " order by obj_" . $resourcekey . ";");
  $i = 0;
  while ($value = mysql_fetch_row ($result)) {
      $url = "resource.php?";
      $url .= "problem=" . $problem . "&";
      $url .= "taskobject=" . $taskobject . "&";
      $url .= "taskkey=" . $taskkey . "&";
      $url .= "resourceobject=" . $resourceobject . "&";
      $url .= "resourcekey=" . $resourcekey . "&";
      $url .= "resourcename=" . urlencode($value[0]);
      echo $i++ . ") " . "<a href=\"" . $url . "\"/>" . $value[0] . "</a><br>";
  }
  mysql_free_result ($result);
?>
  </td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td></td>
  <td></td>
</tr>
<tr>
  <td colspan=3 ALIGN=CENTER>
    <font size=+1>
      <? linkToProblem ($problem); ?>
    </font>
  </td>
</tr>
</table>
<?
  mysql_close();
 } // of mainContent
?>




