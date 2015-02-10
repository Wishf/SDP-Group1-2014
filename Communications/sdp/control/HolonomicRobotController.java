package sdp.control;

import sdp.comms.Radio;
import sdp.comms.packets.*;
import sdp.util.DriveDirection;

import java.io.IOException;

/**
 * Created by Matthew on 09/02/2015.
 */
public class HolonomicRobotController extends BaseRobotController {

    private int[] msPerCm;
    // Grey holonomic wheels are 58mm in diameter
    // http://www.microrobo.com/58mm-lego-compatible-omni-wheel.html
    private static final int WHEEL_DIAMETER = 58;
    private static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
    private static final double WHEEL_DEG_PER_DIST = (double)(360 / WHEEL_CIRCUMFERENCE);
    // Distances from center of wheel to center of axle (back wheel measured from middle of front axle; probably wrong)
    private static final double WHEEL_LEFT_DIST = 57.5;
    private static final double WHEEL_RIGHT_DIST = 57.5;
    private static final double WHEEL_BACK_DIST = 80;

    // Front axle track width
    private static final double TRACK_WIDTH = WHEEL_LEFT_DIST + WHEEL_RIGHT_DIST;
    private static final double TURN_RATIO = TRACK_WIDTH / WHEEL_DIAMETER;

    private static final int GROUP9_WHEEL_DIAMETER = 57;
    private static final double GROUP9_WHEEL_CIRCUMFERENCE = Math.PI * GROUP9_WHEEL_DIAMETER;

    // Ratio to convert speed from group 9 to speed fitting our wheels
    private static final double WHEEL_RATIO = GROUP9_WHEEL_DIAMETER / (double)WHEEL_DIAMETER;

    public HolonomicRobotController(Radio radio,
                                    boolean initialCatcher,
                                    int msPerCm_wheel1,
                                    int msPerCm_wheel2,
                                    int msPerCm_wheel3){
        super(radio, initialCatcher);

        msPerCm = new int[3];
        msPerCm[0] = msPerCm_wheel1;
        msPerCm[1] = msPerCm_wheel2;
        msPerCm[2] = msPerCm_wheel3;
    }

    @Override
    public void onMotionComplete() {

    }
    
    //speed in degrees per second
    public EnqueueMotionPacket rotate(int angle, int speed){
		
    	
    	double turnAngle = angle * TURN_RATIO;
    	
    	int millis = (int) Math.round(turnAngle / 360 * WHEEL_CIRCUMFERENCE * msPerCm[0]);
    	
    	//TODO: calculate the speed actually
    	byte motor1power = (byte) 255;
    	byte motor2power = (byte) 255;
    	
    	DriveDirection leftMotorDir;
    	DriveDirection rightMotorDir;
    	
    	if(angle > 0){
    		leftMotorDir = DriveDirection.FORWARD;
    		rightMotorDir = DriveDirection.BACKWARD;
    	} else {
    		leftMotorDir = DriveDirection.BACKWARD;
    		rightMotorDir = DriveDirection.FORWARD;
    	}
    	
    	return new EnqueueMotionPacket(
    			motor1power, leftMotorDir, 
    			motor2power, rightMotorDir, 
    			(byte) 0, DriveDirection.FORWARD,
    			millis);    	
    }
    
    
    
    public EnqueueMotionPacket travel(int distance, int travelSpeed){
    	
    	
    	int millis = (int) Math.round(distance * msPerCm[0]);
    	
    	//TODO: calculate the speed actually
    	byte motor1power = (byte) 255;
    	byte motor2power = (byte) 255;
    	
    	DriveDirection leftMotorDir;
    	DriveDirection rightMotorDir;
    	
    	if(distance > 0){
    		leftMotorDir = DriveDirection.FORWARD;
    		rightMotorDir = DriveDirection.FORWARD;
    	} else {
    		leftMotorDir = DriveDirection.BACKWARD;
    		rightMotorDir = DriveDirection.BACKWARD;
    	}
    	
    	return new EnqueueMotionPacket(
    			motor1power, leftMotorDir, 
    			motor2power, rightMotorDir, 
    			(byte) 0, DriveDirection.FORWARD,
    			millis); 
    	
    }
    
    
    public EnqueueMotionPacket travelArc(double arcRadius, int distance, int speed){
    	
    	int millis = (int) Math.round(distance * msPerCm[0]);
    	
    	//TODO: calculate the speed actually
    	byte motor1power = (byte) 255;
    	byte motor2power = (byte) 255;
    	
    	DriveDirection leftMotorDir;
    	DriveDirection rightMotorDir;
    	
    	if(distance > 0){
    		leftMotorDir = DriveDirection.FORWARD;
    		rightMotorDir = DriveDirection.FORWARD;
    	} else {
    		leftMotorDir = DriveDirection.BACKWARD;
    		rightMotorDir = DriveDirection.BACKWARD;
    	}
    	
    	return new EnqueueMotionPacket(
    			motor1power, leftMotorDir, 
    			motor2power, rightMotorDir, 
    			(byte) 0, DriveDirection.FORWARD,
    			millis); 
    	
    }
    
}
