import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;

public class Colorsensor {

	static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	public static void main (String [] args) {
		
		Button.waitForAnyPress();
		
		int color_value = colorSensor.getColorID();
		
		if (color_value==1)//black
			//continue moving forward
			
		if (color_value==6)//white
			//move back and turn to continue following the line
		
	}
}
