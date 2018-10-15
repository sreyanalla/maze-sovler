import lejos.hardware.Button;
import lejos.robotics.Color;
import lejos.utility.Delay;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.motor.*;
import lejos.robotics.SampleProvider;
import  lejos.robotics.chassis.Chassis; 
import  lejos.robotics.chassis.Wheel; 
import  lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class RGBtoHSVDemo {
	
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
		
		Wheel wheel1 = WheeledChassis.modelWheel(motora , 2.5).offset(-5.0);
	    Wheel wheel2 = WheeledChassis.modelWheel(motorb , 2.5).offset(5.0);
	    
	    Chassis chassis = new WheeledChassis(new  Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		
		
		//is pressed method? do we need to make float values and fetching samples?
	    MovePilot pilot = new MovePilot(chassis);
	    
		
		
		Button.waitForAnyPress(); 
		while(Button.ESCAPE.isUp())
		{
			rgb = colorSensor.getColor();
			double[] hsv = RGBtoHSV(rgb);
            System.out.println("RGB = "+
                " [" + rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue() 
                +"]\n "  + "HSV = " + "[ " + hsv[0] + "," + hsv[1] + "," + hsv[2] + "," +" ]");
			//delay a second so we don't see so many outputs on the screen
            //need to apply to actual line following strategy
			Delay.msDelay(1000);
			
			 if (hsv[0]<147 && hsv[0]>139) {
				 System.out.println("white space");
				 	/*pilot.travel(-5);
					pilot.rotate(-10);
					pilot.travel(5);
					pilot.rotate(10);
					pilot.forward();*/
				 
			 }
			 else if (hsv[0]<122 && hsv[0]>99) {
				 System.out.println("black line");
				 	/*pilot.travel(-5);
					pilot.rotate(10);
					pilot.travel(5);
					pilot.rotate(-10);
					pilot.forward();*/
				 
				 
			 }
			 else if (hsv[0]<139 && hsv[0]>120) {
				 System.out.println ("middle");
				 /*pilot.forward();*/
			 }
		}
		
			 
		
		//free up resources
		colorSensor.close();
		
	}

}
