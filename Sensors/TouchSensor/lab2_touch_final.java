/**
 * TODO: using a touch sensor to avoid obstacles

  * move forward
  * sense that there is an obstacle (1 instead of 0)
  * stop
  * turn 90 degrees
  * go forward
  * turn 90 degrees
  * move forward
  * obstacle avoided!
 */

/**
 * @author TODO: Sydney Kleingartner, sydneykleingartner
 */
 
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.robotics.navigation.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class lab2_touch_final {
	//declare a new IR sensor connected to port S2
   static EV3IRSensor irSensor = new EVTouchSensor(SensorPort.S2);
   //declare new LargeMotors connected to ports A and C
   static EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.A);
   static EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);
   
	public static void main(String[] args) {
   
      //nothing starts before the master presses a button on the EV3
      Button.waitForAnyPress();
   
      //get an instance of this sensor in measurement mode
      SensorMode touchDistance = touchSensor.getDistanceMode();
      //set up the variable array that holds the distance
      float [] sampleValue = new float [touchDistance.sampleSize()];
   
      // the wheel diameter of the left and right motors is 2.0 cm without tires
      // the value following .offset is the distance between the center of the wheel & the center of the robot
      /* setup the wheel diameter of left (and right) motor  in centimeters (3.0 cm with tires)   
			       the offset number is the distance between the center of wheel 
			       to the center of robot (half of track width)*/
      Wheel wheel1 = WheeledChassis.modelWheel(LEFT_MOTOR, 3.0).offset(6.5);
      Wheel wheel2 = WheeledChassis.modelWheel(RIGHT_MOTOR, 3.0).offset(-6.5);
   
      // set up the  chassis  type:  Differential pilot
	   Chassis chassis = new WheeledChassis(new  Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		
	   MovePilot robot = new MovePilot(chassis);
   
      // obstacle avoidance below
   
      while (Button.getButtons() != Button.ID_ESCAPE) {
		   //drive the robot forward infinitly or untill commanded to stop
			robot.forward();

		   //if you want the robot to travel a certain distance (100 cm for example)
			//negative distance moves the robot in the oposite dirrection
		   //robot.travel(100);

			//if you want to turn 90 degrees
			//robot.rotate(90);
         while (robot.isMoving) {
            // fetch a sample store it at location 0 in the samplevalue array: samplevalue[0]                         
            touchDistance.fetchSample(samplevalue, 0);
            
            if (samplevalue[0] == 1) {
               robot.stop();
               robot.travel(-15);
               //stops after a certain distance
               robot.rotate(90);
               robot.travel(35);
               robot.rotate(-90);
            }
            
            robot.forward();
         } 
      }		      
	}
}