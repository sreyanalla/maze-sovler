package test;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
//import lejos.utility.Delay;

import lejos.hardware.Button;
import lejos.hardware.port.Port;
import lejos.hardware.ev3.LocalEV3;

public class ColorSensorTest {
	
	EV3ColorSensor colorSensor;
	SampleProvider colorProvider;
	float[] colorSample;
	

	public static void main(String[] args) {
		new ColorSensorTest();
	}
	
	public ColorSensorTest() {
		Port s3 = LocalEV3.get().getPort("S3"); 
		colorSensor = new EV3ColorSensor(s3);
		colorProvider = colorSensor.getColorIDMode();
		colorSample = new float[colorProvider.sampleSize()];
		
		while (Button.ESCAPE.isUp()) {
			int detectedColor = colorSensor.getColorID();
			
			if (detectedColor == 1) {
				System.out.println("BLACK");
				//the robot will continue to move forward 
				//pilot.forward()
			}
			
			else if (detectedColor == 6) {
				System.out.println("WHITE");
				//robot should stop
				//look left at 45 degrees
				//look right at 45 degrees
				//if black is detected continue moving forward
			}
		//	switch (currentDetectedColor) {
		//	case Color.BLACK:
		//		break;
		//	case Color.WHITE:
		//		break;
		//	default:
		//		colorSensor.setFloodlight(Color.NONE);
		//		break;
		//	}
		//	Delay.msDelay(500);
		}
		
		colorSensor.close();
	}
}
