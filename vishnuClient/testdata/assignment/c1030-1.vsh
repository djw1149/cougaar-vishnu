<?xml version='1.0'?>
<PROBLEM name="gap_c1030_1" >
<DATAFORMAT>
<OBJECTFORMAT name="agent" is_task="false" is_resource="true" >
<FIELDFORMAT name="id" datatype="string" is_subobject="false" is_globalptr="false" is_list="false" is_key="true" />
<FIELDFORMAT name="index" datatype="number" is_subobject="false" is_globalptr="false" is_list="false" is_key="false" />
<FIELDFORMAT name="capacity" datatype="number" is_subobject="false" is_globalptr="false" is_list="false" is_key="false" />
</OBJECTFORMAT>
<OBJECTFORMAT name="job" is_task="true" is_resource="false" >
<FIELDFORMAT name="id" datatype="string" is_subobject="false" is_globalptr="false" is_list="false" is_key="true" />
<FIELDFORMAT name="index" datatype="number" is_subobject="false" is_globalptr="false" is_list="false" is_key="false" />
<FIELDFORMAT name="costs" datatype="number" is_subobject="false" is_globalptr="false" is_list="true" is_key="false" />
<FIELDFORMAT name="loads" datatype="number" is_subobject="false" is_globalptr="false" is_list="true" is_key="false" />
</OBJECTFORMAT>
</DATAFORMAT>
<SPECS direction="maximize" multitasking="none" >
<OPTCRITERION>
<OPERATOR operation="sumover" >
<LITERAL value="tasks" type="variable" datatype="list:task" />
<LITERAL value="task" type="constant" datatype="string" />
<OPERATOR operation="entry" >
<OPERATOR operation="get" >
<LITERAL value="task" type="variable" datatype="task" />
<LITERAL value="costs" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="list:number" />
</OPERATOR>
<OPERATOR operation="get" >
<OPERATOR operation="resourcefor" >
<LITERAL value="task" type="variable" datatype="task" />
</OPERATOR>
<LITERAL value="index" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="number" />
</OPERATOR>
</OPERATOR>
</OPERATOR>
</OPTCRITERION>
<DELTACRITERION>
<OPERATOR operation="entry" >
<OPERATOR operation="get" >
<LITERAL value="task" type="variable" datatype="task" />
<LITERAL value="costs" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="list:number" />
</OPERATOR>
<OPERATOR operation="get" >
<LITERAL value="resource" type="variable" datatype="resource" />
<LITERAL value="index" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="number" />
</OPERATOR>
</OPERATOR>
</DELTACRITERION>
<CAPACITYCONTRIB>
<OPERATOR operation="get" >
<LITERAL value="task" type="variable" datatype="task" />
<LITERAL value="loads" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="list:number" />
</OPERATOR>
</CAPACITYCONTRIB>
<CAPACITYTHRESH>
<OPERATOR operation="loop" >
<OPERATOR operation="length" >
<LITERAL value="resources" type="variable" datatype="list:resource" />
</OPERATOR>
<LITERAL value="i" type="constant" datatype="string" />
<OPERATOR operation="if" >
<OPERATOR operation="=" >
<LITERAL value="i" type="variable" datatype="number" />
<OPERATOR operation="get" >
<LITERAL value="resource" type="variable" datatype="resource" />
<LITERAL value="index" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="number" />
</OPERATOR>
</OPERATOR>
<OPERATOR operation="get" >
<LITERAL value="resource" type="variable" datatype="resource" />
<LITERAL value="capacity" type="constant" datatype="string" />
<LITERAL value="" type="variable" datatype="number" />
</OPERATOR>
<LITERAL value="100000" type="constant" datatype="number" />
</OPERATOR>
</OPERATOR>
</CAPACITYTHRESH>
<COLORTESTS>
</COLORTESTS>
</SPECS>
<GAPARMS pop_size="1000" parent_scalar="0.99000" max_evals="8000" max_time="120" max_duplicates="50000" max_top_dog_age="50000" report_interval="1" initializer="org.cougaar.lib.vishnu.server.OrderedInitializer" decoder="org.cougaar.lib.vishnu.server.OrderedDecoder" >
<GAOPERATORS>
<GAOPERATOR name="org.cougaar.lib.vishnu.server.OrderedMutation" prob="0.5" parms="1.0" />
<GAOPERATOR name="org.cougaar.lib.vishnu.server.OrderedCrossover" prob="0.5" />
</GAOPERATORS>
</GAPARMS>
<DATA>
<CLEARDATABASE />
<WINDOW />
<NEWOBJECTS>
<OBJECT type="job" >
<FIELD name="id" value="Job 1" />
<FIELD name="index" value="1" />
<FIELD name="costs" >
<LIST>
<VALUE value="25" />
<VALUE value="16" />
<VALUE value="19" />
<VALUE value="17" />
<VALUE value="18" />
<VALUE value="24" />
<VALUE value="20" />
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="17" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="5" />
<VALUE value="25" />
<VALUE value="12" />
<VALUE value="24" />
<VALUE value="15" />
<VALUE value="25" />
<VALUE value="20" />
<VALUE value="15" />
<VALUE value="20" />
<VALUE value="14" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 10" />
<FIELD name="index" value="10" />
<FIELD name="costs" >
<LIST>
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="20" />
<VALUE value="15" />
<VALUE value="24" />
<VALUE value="23" />
<VALUE value="24" />
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="18" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="9" />
<VALUE value="22" />
<VALUE value="17" />
<VALUE value="12" />
<VALUE value="17" />
<VALUE value="9" />
<VALUE value="25" />
<VALUE value="13" />
<VALUE value="13" />
<VALUE value="25" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 11" />
<FIELD name="index" value="11" />
<FIELD name="costs" >
<LIST>
<VALUE value="21" />
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="22" />
<VALUE value="19" />
<VALUE value="25" />
<VALUE value="24" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="10" />
<VALUE value="9" />
<VALUE value="12" />
<VALUE value="9" />
<VALUE value="6" />
<VALUE value="9" />
<VALUE value="25" />
<VALUE value="23" />
<VALUE value="8" />
<VALUE value="24" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 12" />
<FIELD name="index" value="12" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="23" />
<VALUE value="19" />
<VALUE value="23" />
<VALUE value="25" />
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="22" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="23" />
<VALUE value="10" />
<VALUE value="10" />
<VALUE value="14" />
<VALUE value="9" />
<VALUE value="13" />
<VALUE value="14" />
<VALUE value="10" />
<VALUE value="23" />
<VALUE value="23" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 13" />
<FIELD name="index" value="13" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="16" />
<VALUE value="21" />
<VALUE value="24" />
<VALUE value="15" />
<VALUE value="20" />
<VALUE value="15" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="13" />
<VALUE value="7" />
<VALUE value="12" />
<VALUE value="12" />
<VALUE value="15" />
<VALUE value="19" />
<VALUE value="16" />
<VALUE value="7" />
<VALUE value="21" />
<VALUE value="20" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 14" />
<FIELD name="index" value="14" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="16" />
<VALUE value="15" />
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="24" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="13" />
<VALUE value="12" />
<VALUE value="19" />
<VALUE value="18" />
<VALUE value="12" />
<VALUE value="10" />
<VALUE value="25" />
<VALUE value="7" />
<VALUE value="21" />
<VALUE value="12" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 15" />
<FIELD name="index" value="15" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="25" />
<VALUE value="15" />
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="22" />
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="16" />
<VALUE value="15" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="23" />
<VALUE value="6" />
<VALUE value="10" />
<VALUE value="8" />
<VALUE value="20" />
<VALUE value="18" />
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="17" />
<VALUE value="25" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 16" />
<FIELD name="index" value="16" />
<FIELD name="costs" >
<LIST>
<VALUE value="20" />
<VALUE value="19" />
<VALUE value="17" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="25" />
<VALUE value="20" />
<VALUE value="21" />
<VALUE value="16" />
<VALUE value="16" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="14" />
<VALUE value="9" />
<VALUE value="12" />
<VALUE value="19" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="5" />
<VALUE value="18" />
<VALUE value="11" />
<VALUE value="17" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 17" />
<FIELD name="index" value="17" />
<FIELD name="costs" >
<LIST>
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="24" />
<VALUE value="23" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="25" />
<VALUE value="17" />
<VALUE value="19" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="24" />
<VALUE value="5" />
<VALUE value="12" />
<VALUE value="24" />
<VALUE value="9" />
<VALUE value="14" />
<VALUE value="22" />
<VALUE value="21" />
<VALUE value="24" />
<VALUE value="18" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 18" />
<FIELD name="index" value="18" />
<FIELD name="costs" >
<LIST>
<VALUE value="15" />
<VALUE value="18" />
<VALUE value="17" />
<VALUE value="16" />
<VALUE value="17" />
<VALUE value="18" />
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="22" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="17" />
<VALUE value="23" />
<VALUE value="10" />
<VALUE value="13" />
<VALUE value="17" />
<VALUE value="8" />
<VALUE value="22" />
<VALUE value="12" />
<VALUE value="11" />
<VALUE value="20" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 19" />
<FIELD name="index" value="19" />
<FIELD name="costs" >
<LIST>
<VALUE value="25" />
<VALUE value="16" />
<VALUE value="21" />
<VALUE value="22" />
<VALUE value="25" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="15" />
<VALUE value="25" />
<VALUE value="25" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="16" />
<VALUE value="24" />
<VALUE value="7" />
<VALUE value="7" />
<VALUE value="19" />
<VALUE value="9" />
<VALUE value="7" />
<VALUE value="24" />
<VALUE value="12" />
<VALUE value="18" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 2" />
<FIELD name="index" value="2" />
<FIELD name="costs" >
<LIST>
<VALUE value="16" />
<VALUE value="17" />
<VALUE value="19" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="25" />
<VALUE value="21" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="25" />
<VALUE value="14" />
<VALUE value="14" />
<VALUE value="11" />
<VALUE value="9" />
<VALUE value="22" />
<VALUE value="10" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="6" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 20" />
<FIELD name="index" value="20" />
<FIELD name="costs" >
<LIST>
<VALUE value="15" />
<VALUE value="21" />
<VALUE value="20" />
<VALUE value="16" />
<VALUE value="17" />
<VALUE value="16" />
<VALUE value="22" />
<VALUE value="15" />
<VALUE value="21" />
<VALUE value="19" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="21" />
<VALUE value="16" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="14" />
<VALUE value="19" />
<VALUE value="24" />
<VALUE value="10" />
<VALUE value="15" />
<VALUE value="25" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 21" />
<FIELD name="index" value="21" />
<FIELD name="costs" >
<LIST>
<VALUE value="16" />
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="22" />
<VALUE value="17" />
<VALUE value="21" />
<VALUE value="18" />
<VALUE value="20" />
<VALUE value="18" />
<VALUE value="24" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="22" />
<VALUE value="8" />
<VALUE value="5" />
<VALUE value="22" />
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="12" />
<VALUE value="7" />
<VALUE value="19" />
<VALUE value="17" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 22" />
<FIELD name="index" value="22" />
<FIELD name="costs" >
<LIST>
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="19" />
<VALUE value="19" />
<VALUE value="23" />
<VALUE value="22" />
<VALUE value="21" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="17" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="5" />
<VALUE value="20" />
<VALUE value="19" />
<VALUE value="21" />
<VALUE value="13" />
<VALUE value="25" />
<VALUE value="8" />
<VALUE value="20" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 23" />
<FIELD name="index" value="23" />
<FIELD name="costs" >
<LIST>
<VALUE value="22" />
<VALUE value="21" />
<VALUE value="22" />
<VALUE value="17" />
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="25" />
<VALUE value="18" />
<VALUE value="20" />
<VALUE value="16" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="7" />
<VALUE value="17" />
<VALUE value="19" />
<VALUE value="24" />
<VALUE value="25" />
<VALUE value="10" />
<VALUE value="5" />
<VALUE value="20" />
<VALUE value="7" />
<VALUE value="19" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 24" />
<FIELD name="index" value="24" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="19" />
<VALUE value="20" />
<VALUE value="17" />
<VALUE value="23" />
<VALUE value="22" />
<VALUE value="17" />
<VALUE value="19" />
<VALUE value="19" />
<VALUE value="17" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="25" />
<VALUE value="22" />
<VALUE value="21" />
<VALUE value="15" />
<VALUE value="22" />
<VALUE value="5" />
<VALUE value="8" />
<VALUE value="13" />
<VALUE value="8" />
<VALUE value="12" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 25" />
<FIELD name="index" value="25" />
<FIELD name="costs" >
<LIST>
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="19" />
<VALUE value="22" />
<VALUE value="19" />
<VALUE value="15" />
<VALUE value="21" />
<VALUE value="18" />
<VALUE value="23" />
<VALUE value="17" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="17" />
<VALUE value="20" />
<VALUE value="12" />
<VALUE value="12" />
<VALUE value="13" />
<VALUE value="23" />
<VALUE value="12" />
<VALUE value="19" />
<VALUE value="18" />
<VALUE value="5" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 26" />
<FIELD name="index" value="26" />
<FIELD name="costs" >
<LIST>
<VALUE value="18" />
<VALUE value="16" />
<VALUE value="21" />
<VALUE value="19" />
<VALUE value="16" />
<VALUE value="19" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="18" />
<VALUE value="25" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="17" />
<VALUE value="6" />
<VALUE value="6" />
<VALUE value="12" />
<VALUE value="14" />
<VALUE value="17" />
<VALUE value="25" />
<VALUE value="18" />
<VALUE value="22" />
<VALUE value="6" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 27" />
<FIELD name="index" value="27" />
<FIELD name="costs" >
<LIST>
<VALUE value="19" />
<VALUE value="18" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="16" />
<VALUE value="16" />
<VALUE value="24" />
<VALUE value="25" />
<VALUE value="17" />
<VALUE value="19" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="14" />
<VALUE value="20" />
<VALUE value="14" />
<VALUE value="22" />
<VALUE value="24" />
<VALUE value="16" />
<VALUE value="18" />
<VALUE value="10" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 28" />
<FIELD name="index" value="28" />
<FIELD name="costs" >
<LIST>
<VALUE value="20" />
<VALUE value="25" />
<VALUE value="20" />
<VALUE value="16" />
<VALUE value="22" />
<VALUE value="22" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="16" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="24" />
<VALUE value="15" />
<VALUE value="6" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="11" />
<VALUE value="19" />
<VALUE value="7" />
<VALUE value="12" />
<VALUE value="5" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 29" />
<FIELD name="index" value="29" />
<FIELD name="costs" >
<LIST>
<VALUE value="21" />
<VALUE value="20" />
<VALUE value="25" />
<VALUE value="24" />
<VALUE value="22" />
<VALUE value="16" />
<VALUE value="15" />
<VALUE value="25" />
<VALUE value="17" />
<VALUE value="23" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="7" />
<VALUE value="7" />
<VALUE value="19" />
<VALUE value="6" />
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="15" />
<VALUE value="9" />
<VALUE value="18" />
<VALUE value="7" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 3" />
<FIELD name="index" value="3" />
<FIELD name="costs" >
<LIST>
<VALUE value="17" />
<VALUE value="19" />
<VALUE value="15" />
<VALUE value="21" />
<VALUE value="23" />
<VALUE value="20" />
<VALUE value="15" />
<VALUE value="19" />
<VALUE value="16" />
<VALUE value="23" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="21" />
<VALUE value="19" />
<VALUE value="15" />
<VALUE value="18" />
<VALUE value="19" />
<VALUE value="12" />
<VALUE value="18" />
<VALUE value="21" />
<VALUE value="9" />
<VALUE value="10" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 30" />
<FIELD name="index" value="30" />
<FIELD name="costs" >
<LIST>
<VALUE value="19" />
<VALUE value="22" />
<VALUE value="17" />
<VALUE value="17" />
<VALUE value="19" />
<VALUE value="17" />
<VALUE value="25" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="22" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="8" />
<VALUE value="7" />
<VALUE value="24" />
<VALUE value="22" />
<VALUE value="23" />
<VALUE value="5" />
<VALUE value="14" />
<VALUE value="6" />
<VALUE value="17" />
<VALUE value="15" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 4" />
<FIELD name="index" value="4" />
<FIELD name="costs" >
<LIST>
<VALUE value="24" />
<VALUE value="18" />
<VALUE value="25" />
<VALUE value="21" />
<VALUE value="17" />
<VALUE value="18" />
<VALUE value="24" />
<VALUE value="21" />
<VALUE value="24" />
<VALUE value="22" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="11" />
<VALUE value="21" />
<VALUE value="25" />
<VALUE value="14" />
<VALUE value="9" />
<VALUE value="6" />
<VALUE value="7" />
<VALUE value="17" />
<VALUE value="22" />
<VALUE value="23" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 5" />
<FIELD name="index" value="5" />
<FIELD name="costs" >
<LIST>
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="24" />
<VALUE value="19" />
<VALUE value="25" />
<VALUE value="19" />
<VALUE value="20" />
<VALUE value="17" />
<VALUE value="16" />
<VALUE value="23" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="25" />
<VALUE value="22" />
<VALUE value="18" />
<VALUE value="9" />
<VALUE value="13" />
<VALUE value="11" />
<VALUE value="12" />
<VALUE value="7" />
<VALUE value="18" />
<VALUE value="12" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 6" />
<FIELD name="index" value="6" />
<FIELD name="costs" >
<LIST>
<VALUE value="24" />
<VALUE value="23" />
<VALUE value="25" />
<VALUE value="20" />
<VALUE value="21" />
<VALUE value="16" />
<VALUE value="19" />
<VALUE value="16" />
<VALUE value="22" />
<VALUE value="25" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="10" />
<VALUE value="11" />
<VALUE value="25" />
<VALUE value="13" />
<VALUE value="19" />
<VALUE value="25" />
<VALUE value="14" />
<VALUE value="5" />
<VALUE value="7" />
<VALUE value="23" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 7" />
<FIELD name="index" value="7" />
<FIELD name="costs" >
<LIST>
<VALUE value="24" />
<VALUE value="15" />
<VALUE value="16" />
<VALUE value="24" />
<VALUE value="22" />
<VALUE value="22" />
<VALUE value="16" />
<VALUE value="22" />
<VALUE value="16" />
<VALUE value="25" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="15" />
<VALUE value="19" />
<VALUE value="6" />
<VALUE value="18" />
<VALUE value="25" />
<VALUE value="15" />
<VALUE value="12" />
<VALUE value="10" />
<VALUE value="5" />
<VALUE value="12" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 8" />
<FIELD name="index" value="8" />
<FIELD name="costs" >
<LIST>
<VALUE value="17" />
<VALUE value="25" />
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="17" />
<VALUE value="18" />
<VALUE value="24" />
<VALUE value="23" />
<VALUE value="15" />
<VALUE value="22" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="17" />
<VALUE value="14" />
<VALUE value="7" />
<VALUE value="14" />
<VALUE value="16" />
<VALUE value="13" />
<VALUE value="18" />
<VALUE value="10" />
<VALUE value="21" />
<VALUE value="9" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="job" >
<FIELD name="id" value="Job 9" />
<FIELD name="index" value="9" />
<FIELD name="costs" >
<LIST>
<VALUE value="23" />
<VALUE value="25" />
<VALUE value="15" />
<VALUE value="21" />
<VALUE value="20" />
<VALUE value="19" />
<VALUE value="23" />
<VALUE value="16" />
<VALUE value="15" />
<VALUE value="24" />
</LIST>
</FIELD>
<FIELD name="loads" >
<LIST>
<VALUE value="10" />
<VALUE value="12" />
<VALUE value="10" />
<VALUE value="11" />
<VALUE value="14" />
<VALUE value="13" />
<VALUE value="8" />
<VALUE value="25" />
<VALUE value="15" />
<VALUE value="20" />
</LIST>
</FIELD>
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 1" />
<FIELD name="index" value="1" />
<FIELD name="capacity" value="40" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 10" />
<FIELD name="index" value="10" />
<FIELD name="capacity" value="37" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 2" />
<FIELD name="index" value="2" />
<FIELD name="capacity" value="30" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 3" />
<FIELD name="index" value="3" />
<FIELD name="capacity" value="32" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 4" />
<FIELD name="index" value="4" />
<FIELD name="capacity" value="37" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 5" />
<FIELD name="index" value="5" />
<FIELD name="capacity" value="39" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 6" />
<FIELD name="index" value="6" />
<FIELD name="capacity" value="35" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 7" />
<FIELD name="index" value="7" />
<FIELD name="capacity" value="38" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 8" />
<FIELD name="index" value="8" />
<FIELD name="capacity" value="34" />
</OBJECT>
<OBJECT type="agent" >
<FIELD name="id" value="Agent 9" />
<FIELD name="index" value="9" />
<FIELD name="capacity" value="36" />
</OBJECT>
</NEWOBJECTS>
</DATA>
</PROBLEM>
