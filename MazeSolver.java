		import lejos.hardware.Button;
		import lejos.robotics.Color;
		import lejos.utility.Delay;
		import lejos.hardware.port.*;
	   import lejos.hardware.sensor.EV3IRSensor;
	   import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.motor.*;
		import lejos.robotics.SampleProvider;
		import  lejos.robotics.chassis.Chassis; 
		import  lejos.robotics.chassis.Wheel; 
		import  lejos.robotics.chassis.WheeledChassis;
		import lejos.robotics.navigation.MovePilot;
		
		
public class MazeSolver {
	/* group: the beet farm
	   robot name: dwight

	   group members:
	   callie balut (team leader)
	   sreya nalla
	   jackie fine
	   allison forsyth
	   sydney kleingartner
	   
	   date of project presentation:
	   november 8th, 2018
	*/

			
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

			public static void main(String[] args) {
				// TODO Auto-generated method stub
				ColorSensor colorSensor = new ColorSensor(SensorPort.S3);
				colorSensor.setRGBMode();
				Color rgb; 
				
				EV3LargeRegulatedMotor motora = new EV3LargeRegulatedMotor (MotorPort.A);
				EV3LargeRegulatedMotor motorb = new EV3LargeRegulatedMotor (MotorPort.C);
				EV3TouchSensor touchsensor = new EV3TouchSensor (SensorPort.S2);
				EV3IRSensor irSensor = new EV3IRSensor (SensorPort.S1);
				
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
			    
				double [] hsv = new double[3];	
				boolean touched = false;
				
					
					while (!Button.ESCAPE.isDown()) {
						//System.out.println ("starting while loop");
						rgb = colorSensor.getColor();
						hsv= RGBtoHSV(rgb);
						
						 
						 //touch sensor code
						 distancetouch.fetchSample(sampletouch, 0);
							if (sampletouch[0]== 1) {
								
								touched = true;
								pilot.travel(-10);
								pilot.rotate(-180);
								pilot.travel(5);
								//debug to see if it ends up on the correct side of the line
							}
						 
						
			            //System.out.println("RGB = "+
			               // " [" + rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue() 
			               // +"]\n "  + "HSV = " + "[ " + hsv[0] + "," + hsv[1] + "," + hsv[2] + "," +" ]");
						//delay a second so we don't see so many outputs on the screen
			            //need to apply to actual line following strategy
						Delay.msDelay(1000);
					
						//when the robot is on the wood it will rotate and fix itself back on the middle
						 if ((hsv[0]>30)&&(hsv[0]<49)) { //need actual values
							 System.out.println("floor");
								pilot.rotate(-5); 
								
						 }
						 //when the robot is on black it will rotate and fix its self back on the middle
						 else if ((hsv[0]>149)&&(hsv[0]<210)) { //need actual values
							 System.out.println("black line");
								pilot.rotate(5);
								
						}
						 //when the robot is in the middle, it will go forward
						 else if ((hsv[0]>50)&&(hsv[0]<109)) { //need actual values
							 System.out.println ("middle");
							 pilot.travel(5);
						 }
						 else {
							 System.out.println (hsv[0]);
						 }
						 
						 //need to implement when the robot sees a new color (red) then it will follow the intersection strategy
						 if ((hsv[0]>7)&&(hsv[0]<11)) {//red
							 
	                     //ir code activated
	      					distanceir.fetchSample(sampleir, 0);
	      					if (sampleir[0]<=8) {
	                              //do not turn
	                              //put another statements saying if it's NOT activated
	                              //if its  not activated then turn right over intersections
	      						  //travels back on to the line when it travels 5
	                            pilot.travel(5);
	      					}  
	      					//attach stacks to the movements
	      					//straight = 0;
	      					//inside conditional
	      					
	      					// rotates to the right if the right is clear
		                     else {
		                            pilot.travel(2);
		                            pilot.rotate(-90);
		                     }	
	      					//attach stacks to the movements
	      					//turn right = 2
	      					//inside conditional
	      					if (touched==true) {
	      						//all combinations go here
	      						touched=false;
	      					}
	      					
						 }
 
						 //when the robot sees silver, then it should recognize that it is at the end of the maze and call to the stacking method to retrace its steps
						 else if ((hsv[0]>110)&&(hsv[0]<135)){ //SILVER
							 //rotate 180* and call the stack
						 }
						 
							
						
	                 //if (touch is pressed) {
	                     //pilot.rotate(180); //turn all the way around
	                     //pilot.forward();  
	                  //} when the touch sensor is pressed
						 
				}
					
					//add hsv for silver end of the maze, and add red for intersections
					/*if the robot sees red and cannot turn to the right (ir sensor) then go straight
					 * 	then if it cant go forward, turn around and when you get back to the red line then turn left
					 * if the robot sees red and can turn right, then turn right
					 * if the robot hits something in front of itself, then have the robot turn around to the right				
					 * 
					 */
					
	            //assign int values to movements of the robots
	            //goal: push these values to the stack to be able to switch when the robot is going back
	            //moving forward (0) would not switch when coming back
	            //int 0 = robot.forward();
	            //moving left (1) would switch to moving right (2) when the robot is going back
	            //int 1 = robot.rotate(90); left
	            //moving right (2) would switch to moving left(1) when the robot is going back
				//int 2 = robot.rotate(-90); right
	            
	            //after a sequence (movement between intersections) is completed:
	            //either a 0 (forward), 1 (left), or 2 (right) is pushed to the stack
	            //if the touch sensor was pressed, that sequence is popped off the stack
	            //when the maze is completed (the silver color is picked up by the color sensor)
	            //1's would be switched to 2's, 2's would be switched to 1's, and 0's would stay the same
	            
	            
	            //find a way to know where the robot came from
	            //ideas: 
	            //if a dead end is reached (touch sensor is pressed), that sequence is popped off the stack
	               //the robot assigns a number or value (maybe we should do a letter like a, b, and so on?) to each intersection
	               //with this, if the robot came back to intersection a (for example), a would again be printed on the screen
	               //is there a way to store these values? look into this
					 
				
				//free up resources
				colorSensor.close();
				
			}

		}
		




