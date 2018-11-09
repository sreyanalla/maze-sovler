 import java.util.Stack;
	import lejos.hardware.Button;
	import lejos.robotics.Color;
	import lejos.utility.Delay;
	import lejos.hardware.port.*;
	import lejos.hardware.Sound;
   import lejos.hardware.sensor.EV3IRSensor;
   import lejos.hardware.sensor.EV3TouchSensor;
   import lejos.hardware.sensor.SensorMode;
   import lejos.hardware.motor.*;
	import lejos.robotics.SampleProvider;
	import  lejos.robotics.chassis.Chassis; 
	import  lejos.robotics.chassis.Wheel; 
	import  lejos.robotics.chassis.WheeledChassis;
	import lejos.robotics.navigation.MovePilot;

	public class RGBtoHSVLineMazeFollow2 {
		
		public static double[] RGBtoHSV(Color colors){
			double[] hsv = new double[3];
			// read colors
			int r = colors.getRed();
			int b = colors.getBlue();
			int g = colors.getGreen();
			
			double min = Math.min(r, Math.min(b,g));
			double max = Math.max(r, Math.max(b, g));
			double delta = max - min;
			hsv[2] = max/255; //set v to max as a percentage
			if (max != 0){
				hsv[1] = delta/max;
			}
			else{ //r = b = g =0 
				hsv[1] = 0; //s = 0;		// s = 0, v is undefined
				hsv[0] = -1; //h = -1;
				return hsv;
			}
			
			if (r == max){
				hsv[0] = (g-b)/delta; //h 
			}
			else{
				if (g == max)
					hsv[0] = 2 + (b - r)/delta; //h
				else
					hsv[0] = 4 + (r - g)/delta; //h
			}
			
			hsv[0] *=60;	//degrees
			if (hsv[0] < 0)
				hsv[0] +=360;
			
			return hsv;
		}

		public static void main(String[] args)throws InterruptedException{
			// TODO Auto-generated method stub
			ColorSensor colorSensor = new ColorSensor(SensorPort.S3);
			colorSensor.setRGBMode();
			Color rgb; 
			
         //importing the sensors and their ports
			EV3LargeRegulatedMotor motora = new EV3LargeRegulatedMotor (MotorPort.A);
			EV3LargeRegulatedMotor motorb = new EV3LargeRegulatedMotor (MotorPort.C);
			EV3TouchSensor touchsensor = new EV3TouchSensor (SensorPort.S2);
			EV3IRSensor irSensor = new EV3IRSensor (SensorPort.S1);
			
         //setting the offsets
			Wheel wheel1 = WheeledChassis.modelWheel(motora , 2.5).offset(-5.0);
		    Wheel wheel2 = WheeledChassis.modelWheel(motorb , 2.5).offset(5.0);
		    
		    Chassis chassis = new WheeledChassis(new  Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		    
		  //distance fetch sample code
		    SensorMode distanceir = irSensor.getDistanceMode();
		    float[] sampleir = new float[1];
		    
			//touch sensor array code
		    SampleProvider distancetouch = touchsensor.getTouchMode();				
			float[] sampletouch = new float[1];
		    
			//is pressed method? do we need to make float values and fetching samples?
		    MovePilot pilot = new MovePilot(chassis);
		    
		    Button.waitForAnyPress(); 
		    
		    //create a new array with the 3 HSV values
			double [] hsv = new double[3];
			
         //created a new stack called stack
         //used later at intersections
			Stack<Integer> stack = new Stack<Integer>();
         
            //initialize the boolean touched variable to false
            boolean touched = false; 
            
            //maze starts off as not solved
            boolean mazeSolved = false;
            
            //initialize the top of the stack (stack peek)
            int topStack;
            
            
			
			//if (mazeSolved == false) {	
				
            while (!Button.ESCAPE.isDown()) {
            	rgb = colorSensor.getColor();
				hsv= RGBtoHSV(rgb);
				distancetouch.fetchSample(sampletouch, 0);
				
            	if(mazeSolved == false) {
					System.out.println("maze not solved");
					System.out.println(hsv[0]);
					if (sampletouch[0]== 1) {
						
                  //if it touches then the robot turns around at the dead ends
						touched = true;
						pilot.travel(-5);
						pilot.rotate(-160);
						pilot.travel(5);							
					}
                 
                //if the robot sees red then it will either turn right or move forward and stack values respectfuly
					 if ((hsv[0]>0)&&(hsv[0]<10)) {
						 
						 System.out.println("red intersection");
						 if (touched != true) {
							 System.out.println("NOT touched after red");
		                     //ir code activated
		      					distanceir.fetchSample(sampleir, 0);
		      					if (sampleir[0]<=20) {
	                                 //measured distance between edge of intersection square and wall => 20 cm
		                              //do not turn
		                              //if its  not activated then turn right over intersections
		      						//travels back on to the line when it travels 5
		                            pilot.travel(5);
		                            stack.push(0);
		      					}  
		      					//attach stacks to the movements
		      					//straight = 0;
		      					//inside conditional
		      					
		      					// rotates to the right if the right is clear
			                     else {
			                    	 	pilot.travel(3);
			                            pilot.rotate(-60);
			                            pilot.travel(2);
			                            stack.push(1);
			                     }	
		      					//attach stacks to the movements
		      					//turn right = 2
		      					//inside conditional
		      					
						 }
						 else if (touched==true) {
	                           touched = false;
	                           System.out.println("dead end reached");
	                           if (stack.isEmpty()) {
	                        	   stack.push(2);
	                        	   pilot.rotate(60);
	                           }
                              
                              else if (!stack.isEmpty()) {
	                        	   stack.pop();
                              }
	                        	   
	                              //touched is now assigned with false
	                         
	                              //peek = tells you the top element in the stack 
	                           
	                           //pop the top of the stack
	                           stack.pop();
	                           
	                           if (stack.peek() == 0) {
	                        	  System.out.println("Last went straight");
	                              //turn right
	                              pilot.rotate(-60);
	                              //store as left (1) on stack
	                              stack.push(1);
	                              System.out.println("Stored left");
	                           }
	                           else if (stack.peek() == 2) {
	                        	  System.out.println("Last went right");
	                              //check right
	                              if (sampleir[0]<=20) { // IR < 20
	                                 pilot.forward();
	                                 stack.push(0);
	                                 System.out.println("Stored straight");
	                                 //add 0 to the stack
	                              }
	                              else {
	                                 //turn right
	                                 pilot.rotate(-60);
	                                 //store as straight (0) on stack
	                                 stack.push(0);
	                                 System.out.println("Stored straight");
	                              }
	                           }
	                        }
					 }
					 
					//if the robot sees silver then maze solved is true and we begin poping off the stack 
	                   else if (((hsv[0]>81)&&(hsv[0]<90))||((hsv[0]>106)&&(hsv[0]<=118))) { 
	                	   
	                	   System.out.println("Silver detected turn around");
	                	   mazeSolved = true;
	                	   System.out.println("Maze Solved");
	                	   
	                	   pilot.travel(-.2);
	                	   pilot.rotate(-160);
	                	   pilot.travel(10);
	                	   pilot.rotate(60);
	                  }	
	                   
				}
			//testing the maze back
				//comment me out after testing
           
           // mazeSolved = true;
           
					 
            	else if(mazeSolved == true) {
	                	//System.out.println("you did it");
	                	if(sampletouch[0] == 1 || stack.isEmpty()) { // take the or stack statement out after debugging
	                		System.out.println("THE END!!");
	                		Sound.beepSequence();
	                		pilot.stop();
	                		break;//exit the loop end of program
	                		
	  					}
	  					 else if ((hsv[0]>=1)&&(hsv[0]<10)) { 
	                	   //red is detected
	  						
	  						 if (stack.isEmpty()) {
	  							 System.out.println("EMPTY");
	  						 }
	  						 else {
	  						 	//pilot.travel(3);
	  						 	System.out.println("red on the way back" + hsv[0]);
	  						 	Thread.sleep(1000);
	  						 	topStack = stack.peek();
	  						 	//forward
	                	   		if (topStack==0) {
	                	   			System.out.println("go straight");
	                	   			pilot.travel(5);
	                	   			stack.pop();
	                	   			topStack = 3;//set to something else
	                	   		}
	                	   		//left
	                	   		if (topStack==1) {
	                	   			System.out.println("go left");
	                	   			pilot.travel(3);
		                            pilot.rotate(90);
		                            pilot.travel(2);
		                            stack.pop();
		                            topStack = 3;//set to something else
	                	   		}
	                	   		
	                	   		if (topStack ==2) {
	                	   			System.out.println("go right");
	                	   			pilot.travel(3);
		                            pilot.rotate(-60);
		                            pilot.travel(2);
		                            stack.pop();
		                            topStack = 3;//set to something else
	                	   		}
	  					}
	  					 
	  					
	  					 }
	                }
	               

			
			//LINE FOLLOWING STRATEGY:
					
				//when the robot is on the middle it will go forward
				 if ((hsv[0]>14)&&(hsv[0]<20)||(hsv[0]>25)&&(hsv[0]<30)||(hsv[0]>40)&&(hsv[0]<61)) { 
					 System.out.println("middle " +hsv[0]);
					 pilot.travel(3);
					 //System.out.println (hsv[0]);
						
				 }
				 //when the robot is on black it will rotate and fix its self back on the middle
				 else if ((hsv[0]>=90)&&(hsv[0]<106)||(hsv[0]>118)&&(hsv[0]<241)) {//||(hsv[0]==0.0)||(hsv[0]>50&&hsv[0]<125)) {
					 System.out.println("black line " +hsv[0]);
						pilot.rotate(2);
						//System.out.println (hsv[0]);
						
				}
				 //when the robot is in the wood, it will fix itself back on the line
				 else if ((hsv[0]>=20)&&(hsv[0]<25)||(hsv[0]>=30)&&(hsv[0]<=40)) {
				 
					 System.out.println ("floor " +hsv[0]);						 
					 pilot.rotate(-6); 						
					 //System.out.println (hsv[0]);
					 
				 }
				 
				 else {
	                	//System.out.println (hsv[0] + "correction");
	   					pilot.travel(.2);
	   				}
	                	   
	                	   //if the color detected is equal to the color of the silver box (ending indicator)
	                     //may need to use ambient light mode
	                     //when color reaches the foil (silver)
	                        //then indicate that the robot should executing what's stacked
	                     
	                     //go through the stack but SWITCHED
	                     //add another boolean variable (mazeSolved) -> indicated maze has now been solved
	                     
	                     //use line following
	                     //structure:
	                     //if (mazeSolved == true)
	                        //switch stack & go back through the maze
	                           //create case statements
	                              //look at powerpoint
	                              //case 0 = ...
	                              //1 should be turned to 2 and vice versa
	                        //1 and 2 will mean the opposite of what they meant when we were going through the maze from start to finish
	                     //else if (mazeSolved = false)
	                        //go through intersection code (red color)
	                     
	                     		
					 
					 
		      				   //all combinations are above (only 3 are relevant)
			                   
      }
			//free up resources
			colorSensor.close();
	}
}
	

