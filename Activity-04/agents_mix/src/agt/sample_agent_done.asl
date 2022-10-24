// Agent sample_agent in project test2

/* Initial beliefs and rules */

language("ita").

// language("jap").


/* Initial goals */

// !hello_world("Cesena").

!mygoal(5).


+!hello_world(X) : language("ita")
 <- .println("Ciao");
    .println("Mondo");
    .println("da ", X).

+!hello_world(X) : language("en")
 <- .println("Hello world! ", X).
 
+!hello_world(X) : language(L)
 <- .println("Don't know the lang ", L).
 
 // 
 
 +!mygoal(X) 
 	<- .println("act1");
 	   !subgoal1(X,Z);
 	   !subgoal2(X,"bb");
 	   .println("done: ", Z).
 	   
-!mygoal(_)
	<- .println("sorry, no way").
		 
+!subgoal1(X,Z)
	<- .println("subg 1 => ", X);
	   Z = "hello".
	   
+!subgoal2(X,Z)
	<- .println("subg2 => ", X, " ", Z).
	



{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
