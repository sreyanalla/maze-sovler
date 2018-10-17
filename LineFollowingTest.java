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

public class LineFollowingTest {
	
	public static void main (String[] argv) {
	
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
		int detectedColor = 0;
		
		detectedTapeColor = colorSensor.getColorID(); 
		 
		pilot.rotate(10);
		detectedFloorColor = colorSensor.getColorID();
		pilot.rotate(-10);
		
		while (Button.ESCAPE.isUp()) {
				
			detectedColor = colorSensor.getColorID();
				
			if(detectedColor == detectedTapeColor) {
				pilot.travel(5);
					
			}
			if(detectedColor == detectedFloorColor) {
				//turn right 5 degrees
				pilot.rotate(-5);
				//move forward
			}
		}
		
		colorSensor.close();
      
	}
}

