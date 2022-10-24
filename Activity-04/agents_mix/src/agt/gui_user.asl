
!testGUI.

+!testGUI 
	<- makeArtifact("c0", "acme.MyGUI", [], Id);
	   println("> ", Id);
	   +num_stopped(0);
	   +value(0);
	   focus(Id);
	   .wait(5000);
	   !longTask.
	   
+!longTask
	<- ?value(N);
	   println("current: ", N);
	   -+value(N+1);	   
	   .wait(1000);
	   !longTask.

+stopped : not .intend(longTask)
	<- ?num_stopped(N);
	   -+num_stopped(N + 1);
	   M = N + 1;
	   println("number of stop signals: ", M).
	

+stopped : .intend(longTask)
	<- .drop_intention(longTask);
	   println("intention dropped.");
	   !anotherTask.

+suspended : .intend(longTask)
	<- .suspend(longTask);
	   println("intention suspended.");
	   !anotherTask;
	   .add_plan("+resumed <- .resume(longTask); println('this is the new one.'). ").
	  

/* 
+resumed 
	<- .resume(longTask). 
*/

	   
+!anotherTask
	<- println("let's go with another task..'");
	   .wait(3000).
		   
	   
	   
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
