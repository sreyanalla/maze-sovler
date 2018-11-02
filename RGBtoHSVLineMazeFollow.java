
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

	public class RGBtoHSVLineMazeFollow {
		
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
		    
			boolean touched = false;
		    
			double [] hsv = new double[3];			
			
				
				while (!Button.ESCAPE.isDown()) {
					//System.out.println ("starting while loop");
					rgb = colorSensor.getColor();
					hsv= RGBtoHSV(rgb);
					
					
					//touch sensor code
					 distancetouch.fetchSample(sampletouch, 0);
						if (sampletouch[0]== 1) {
							
							touched = true;
							pilot.travel(-5);
							pilot.rotate(-160);
							pilot.travel(5);							
							//debug to see if it ends up on the correct side of the line
						}
					
					//Delay.msDelay(100);
					
					//make the middle range smaller?
				
					//when the robot is on the middle it will go forward
					 if ((hsv[0]>30)&&(hsv[0]<34)||(hsv[0]>=47)&&(hsv[0]<61)) { 
						 System.out.println("middle");
						 pilot.travel(3);
						 System.out.println (hsv[0]);
							
					 }
					 //when the robot is on black it will rotate and fix its self back on the middle
					 else if ((hsv[0]>=90)&&(hsv[0]<200)) {//||(hsv[0]==0.0)||(hsv[0]>50&&hsv[0]<125)) {
						 System.out.println("black line");
							pilot.rotate(5);
							System.out.println (hsv[0]);
							
					}
					 //when the robot is in the wood, it will fix itself back on the line
					 else if ((hsv[0]>=34)&&(hsv[0]<47)) {
					 
						 System.out.println ("floor");						 
						 pilot.rotate(-6); 						
						 System.out.println (hsv[0]);
						 
					 }

				
					 
					 //need to implement when the robot sees a new color (red) then it with turn or do as it is called to to
					 else if ((hsv[0]>5)&&(hsv[0]<10)) {
						 
						 System.out.println("red intersection");
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
		                            pilot.travel(3);
		                            pilot.rotate(-80);
		                            pilot.travel(3);
		                     }	
	      					//attach stacks to the movements
	      					//turn right = 2
	      					//inside conditional
	      					if (touched==true) {
	      						//all combinations go here
	      						touched=false;
	      					}
					 }
					 
					 else {
						 System.out.println("hey");
						 pilot.travel(.2);
					 }
					 //when the robot sees silver, then it should recognize that it is at the end of the maze and call to the stacking method to retrace its steps
					 /*else if ((hvs[0]>110)&&(hsv[0]<135)){ SILVER
					  * 83-95
					 }*/
					 
					 
					 //touch sensor code
					 /*distance.fetchSample(sample, 0);
						if (sample[0]== 1) {
							
							pilot.travel(-10);
							pilot.rotate(-90);
							pilot.travel(20);
							pilot.rotate(90);
							pilot.forward();
						}
						
					//ir code
					distance.fetchSample(sample, 0);
						if (sample2[0]<=18) {
							
							pilot.travel(-10);
							pilot.rotate(-90);
							pilot.travel(20);
							pilot.rotate(90);
							pilot.forward();
						}*/
					 
			}
				
				//add hsv for silver end of the maze, and add red for intersections
				/*if the robot sees red and cannot turn to the right (ir sensor) then go straight
				 * 	then if it cant go forward, turn around and when you get back to the red line then turn left
				 * if the robot sees red and can turn right, then turn right
				 * if the robot hits something in front of itself, then have the robot turn around to the right				
				 * 
				 */
				
			
			
				 
			
			//free up resources
			colorSensor.close();
			
		}

	}
	


