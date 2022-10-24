/* Initial beliefs and rules */

// language("Ita").
language("Eng").

/* Initial goals */

!greet_the_world.

/* plans */

// greet the world once

+!greet_the_world : language ("Eng") <-
	println("Hello world!").

+!greet_the_world : language ("Ita") <-
	println("ciao mondo!").
	
// examples with parameters and subgoals
	
+!greet_the_world_multiple_times(NTimes) : NTimes > 0 <-
    !greet_the_world;
	N is NTimes - 1;
	!greet_the_world_multiple_times(N).

+!greet_the_world_multiple_times(0).


















/* for JaCaMo integration */

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

