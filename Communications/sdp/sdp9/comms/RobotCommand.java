package sdp.sdp9.comms;

import java.io.DataOutputStream;
import java.io.IOException;
import sdp.comms.packets.*;
import sdp.util.DriveDirection;
import sdp.comms.Radio;

public class RobotCommand {
    private static byte FORWARD_SPEED = (byte) 200;
    private static byte BACKWARD_SPEED = (byte) 200;
    private static byte STOP_SPEED = (byte) 0;
    private static int TIME_TO_MOVE_90_DEGREES = 500;
    private static byte ROTATION_SPEED = (byte) 150;
	private RobotCommand() {
	}

	public interface Command {
		public void sendToBrick(Radio radio)
				throws IOException;
	}

	private static abstract class GenericCommand implements Command {
		protected abstract Packet getOpcode();

		@Override
		public void sendToBrick(Radio radio)
				throws IOException {
			radio.sendPacket(getOpcode());
		}
	}
	
	// Classes below represent every possible brick command

	public static class Stop extends GenericCommand {
		@Override
		protected Packet getOpcode() {
			return new ClearQueuePacket();
		}
	}

	public static class Forwards extends GenericCommand {
		@Override
		protected Packet getOpcode() {
			return new EnqueueMotionPacket(FORWARD_SPEED, DriveDirection.FORWARD, FORWARD_SPEED, DriveDirection.FORWARD, STOP_SPEED, DriveDirection.FORWARD, 65000);
		}
	}

	public static class Backwards extends GenericCommand {
		@Override
		protected Packet getOpcode() {
            return new EnqueueMotionPacket(BACKWARD_SPEED, DriveDirection.BACKWARD, BACKWARD_SPEED, DriveDirection.BACKWARD, STOP_SPEED, DriveDirection.FORWARD, 65000);
		}
	}

	public static class Kick extends GenericCommand {
		private byte speed;

		public Kick(byte speed) {
			this.speed = speed;
		}

		@Override
		protected Packet getOpcode() {
			return new KickPacket(speed);
		}
	}

	public static class Catch extends GenericCommand {
		@Override
		protected Packet getOpcode() {
            // TODO!!!
            return new ErrorPacket();
		}
	}


	public static class RotateLeft extends GenericCommand {
		@Override
		protected Packet getOpcode() {
            return new EnqueueMotionPacket(STOP_SPEED, DriveDirection.FORWARD, STOP_SPEED, DriveDirection.FORWARD,
                    ROTATION_SPEED, DriveDirection.FORWARD, TIME_TO_MOVE_90_DEGREES);
		}
	}

	public static class RotateRight extends GenericCommand {
		@Override
		protected Packet getOpcode() {
            return new EnqueueMotionPacket(STOP_SPEED, DriveDirection.FORWARD, STOP_SPEED, DriveDirection.FORWARD,
                    ROTATION_SPEED, DriveDirection.BACKWARD, TIME_TO_MOVE_90_DEGREES);
		}
	}

	public static class Rotate extends GenericCommand {
		private int angle;
        private DriveDirection direction;
		private byte speed;

		public Rotate(int angle, byte speed, boolean immediateReturn){
            if(angle > 0) {

            } else {

            }
			this.angle = angle;
			this.speed = speed;
		}
		
		public Rotate(int angle, byte speed) {
			this(angle, speed, true);
		}

		@Override
		protected Packet getOpcode() {

            return new EnqueueMotionPacket(STOP_SPEED, DriveDirection.FORWARD, STOP_SPEED, DriveDirection.FORWARD,
                    speed, DriveDirection.FORWARD, TIME_TO_MOVE_90_DEGREES);
		}
	}

	public static class TravelArc extends GenericCommand {
		private double arcRadius;
		private int distance;
		private int speed;

		public TravelArc(double arcRadius, int distance, int speed) {
			this.arcRadius = arcRadius;
			this.distance = distance;
			this.speed = speed;
		}

		@Override
		protected int getOpcode() {
			return RobotOpcode.ARC_FORWARDS;
		}

		@Override
		public void sendToBrick(DataOutputStream outputStream)
				throws IOException {
			super.sendToBrick(outputStream);
			outputStream.writeDouble(arcRadius);
			outputStream.writeInt(distance);
			outputStream.writeInt(speed);
		}
	}

	public static class Travel extends GenericCommand {
		private int distance;
		private int travelSpeed;
		
		public Travel(int distance, int travelSpeed) {
			this.distance = distance;
			this.travelSpeed = travelSpeed;
		}
		
		@Override
		protected int getOpcode() {
			return RobotOpcode.TRAVEL;
		}
		
		@Override
		public void sendToBrick(DataOutputStream outputStream)
				throws IOException {
			super.sendToBrick(outputStream);
			outputStream.writeInt(distance);
			outputStream.writeInt(travelSpeed);
		}
	}
	
	public static class ResetCatcher extends GenericCommand {
		@Override
		protected int getOpcode() {
			return RobotOpcode.RESET_CATCHER;
		}
	}
}
