import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class LineFollowingTestFinal {
	
	public static void main (String[] argv) {
		System.out.println("Press any key to start");
		Button.waitForAnyPress();
		
		EV3ColorSensor colorSensor;
		SampleProvider colorProvider;
		
		EV3LargeRegulatedMotor  LEFT_MOTOR  =  new  EV3LargeRegulatedMotor(MotorPort.A); 
	    EV3LargeRegulatedMotor  RIGHT_MOTOR  =  new  EV3LargeRegulatedMotor(MotorPort.C);
	   
	    
	     Wheel wheel1 = WheeledChassis.modelWheel(LEFT_MOTOR , 2.5).offset(-5);
	     Wheel wheel2 = WheeledChassis.modelWheel(RIGHT_MOTOR , 2.5).offset(5);
	      
	     Chassis chassis = new WheeledChassis(new  Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
	      
	     MovePilot pilot = new MovePilot(chassis);
	      
	     Port s3 = LocalEV3.get().getPort("S3"); 
	     colorSensor = new EV3ColorSensor(s3);
	     colorProvider = colorSensor.getColorIDMode();
		
		int detectedTapeColor = 0;
		int detectedFloorColor = 0;
		int detectedMidColor = 0;
		int detectedColor = 0;
		
		detectedTapeColor = colorSensor.getColorID();
		
		pilot.rotate(-20);
		detectedMidColor = colorSensor.getColorID(); 
		pilot.rotate(20);
		 
		pilot.rotate(40);
		detectedFloorColor = colorSensor.getColorID();
		pilot.rotate(-40);
		
		while(Button.getButtons() != Button.ID_ESCAPE) {
				
			detectedColor = colorSensor.getColorID();
				
			if(detectedColor == detectedMidColor) {
				pilot.travel(5);
					
			}
			if((detectedColor == detectedFloorColor) || (detectedColor == detectedTapeColor)) {	
				pilot.rotate(-5);
			}
			
			else {
				pilot.rotate(-5);
			}
		}
		
		colorSensor.close();
      
	}
}

