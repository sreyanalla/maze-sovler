import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;
import ev3.exercises.library.ColorSensor;
import ev3.exercises.library.Lcd;

public class ColorDemo
{
	static float[] sample;
    static EV3ColorSensor    colorSensor = new EV3ColorSensor(SensorPort.S3);
    public static void main(String[] args)
    {
 

        System.out.println("Color Demo");
        Lcd.print(2, "Press to start");
        
        Button.LEDPattern(4);    // flash green led and
        Sound.beepSequenceUp();    // make sound when ready.

        Button.waitForAnyPress();
        Button.LEDPattern(0);
        
        while(Button.getButtons() != Button.ID_ESCAPE)
        {
        	setRGBMode();
           
           System.out.println();
        }
        colorSensor.close();
        
        Sound.beepSequence();    // we are done.

        Button.LEDPattern(4);
        Button.waitForAnyPress();
    }
    public static void setRGBMode()
	{
		
		colorSensor.setCurrentMode("RGB");
		sample = new float[colorSensor.sampleSize()];
		System.out.println(sample[0] + " Red");
		System.out.println(sample[1] + "Green");
		System.out.println(sample[2] + "Blue");	
		
	}

}
