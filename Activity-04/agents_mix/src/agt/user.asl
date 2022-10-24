
+!test 
	<- makeArtifact("c0", "acme.Counter", [13], Id);
	   .println("> ", Id);
	   focus(Id);
	   inc [artifact_id(Id)] ;
	   .println("first inc done");
	   .wait(1000);
	   inc;
	   .println("second inc done").

@react_to_count_plan [atomic]
+count(X)
	<- .println("new count value: ", X);
	   .wait(1500);
	   .println("reactive plan completed.").
	   
+tick(X,Y)
	<- println("new tick ", X, Y).
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
