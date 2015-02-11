package sdp.sdp9.strategy;

import java.util.ArrayDeque;
import java.util.Deque;

import sdp.sdp9.comms.BrickCommServer;
import sdp.sdp9.comms.RobotCommand;
import sdp.sdp9.strategy.interfaces.Strategy;
import sdp.sdp9.vision.Vector2f;
import sdp.sdp9.world.oldmodel.MovingObject;
import sdp.sdp9.world.oldmodel.WorldState;

public class LateNightStrategy extends GeneralStrategy {

	
	private BrickCommServer brick;
	private ControlThread controlThread;
	private Deque<Vector2f> ballPositions = new ArrayDeque<Vector2f>();

	public LateNightStrategy(BrickCommServer brick) {
		this.brick = brick;
		this.controlThread = new ControlThread();
	}

	@Override
	public void stopControlThread() {
		this.controlThread.stop();
	}

	@Override
	public void startControlThread() {
		this.controlThread.start();
	}
	
	@Override
	public void sendWorldState(WorldState worldState) {
		super.sendWorldState(worldState);
		// Calculate
		
		MovingObject ball = worldState.getBall();
		MovingObject robot = worldState.getDefenderRobot();
		
		
		boolean ballInAttackerArea = false;
		ballPositions.addLast(new Vector2f(ball.x, ball.y));
		if (ballPositions.size() > 3)
			ballPositions.removeFirst();

		if (ballX > leftCheck && ballX < rightCheck) {
			ballInAttackerArea = true;
		}
		
		Vector2f ball3FramesAgo = ballPositions.getFirst();
		float ballX1 = ball3FramesAgo.x, ballY1 = ball3FramesAgo.y;
		float ballX2 = worldState.getBall().x, ballY2 = worldState.getBall().y;

		double slope = (ballY2 - ballY1) / ((ballX2 - ballX1) + 0.0001);
		double c = ballY1 - slope * ballX1;
		boolean ballMovement = Math.abs(ballX2 - ballX1) < 10;
		//int targetY = (int) (slope * defenderRobotX + c);
		//double ang1 = calculateAngle(defenderRobotX, defenderRobotY,
		//		defenderOrientation, defenderRobotX, defenderRobotY - 50);
		//ang1 = ang1 / 3;
				
		// 1. Check if the robot needs to rotate
		
		//System.out.println("Orientation " + defenderOrientation + "; " + attackerOrientation);
		
		boolean rotate_defender = false;
		double angleRR = defenderOrientation;
		double targetAngle = 0;
		double angleDifference = (defenderOrientation - targetAngle) % 360;
		
		//System.out.println("Angle difference: "+angleDifference);
		
		if(angleDifference > 30.0 && angleDifference < 330.0 ) {
			rotate_defender = true;
			//System.out.println("Need to rotate the robot because orientation=" + defenderOrientation);
			if(angleRR > 180) {
				angleRR -= 360;
			}
		}
		
		
		boolean move_robot = false;
		
		double dX = ball3FramesAgo.x - defenderRobotX;
		int targetY = (int) (slope * dX + c);

		//System.out.println("Ball X: " + ball.x + " y " + ball.y);
		//System.out.println("Robot x" + defenderRobotX + " y " + defenderRobotY);
		int dY = (int) (targetY - defenderRobotY);
		if(Math.abs(dY) > 5) {
			move_robot = true;
			//System.out.println("Need to move the robot since dY=" + dY);
		}
		
		
		boolean move_back = false;
		
		double checkDx = defenderRobotX - defenderCheck;
		//System.out.println(checkDx);
		if(Math.abs(checkDx) < 40 ){
			move_back = true;
			//System.out.println("Move back");
		}
		
		
		synchronized (this.controlThread) {
			this.controlThread.operation.op = Operation.Type.DO_NOTHING;
			
			if(rotate_defender) {
				this.controlThread.operation.op = Operation.Type.DEFROTATE;
				controlThread.operation.rotateBy = (int) (angleRR);
			} 
			else if(move_back) {
				this.controlThread.operation.op = Operation.Type.DEBACK;
				controlThread.operation.travelDistance = (int) Math.min(-10, -(40-Math.abs(checkDx)));
			}
			else if(move_robot) {
				this.controlThread.operation.op = Operation.Type.DESIDEWAYS;
				controlThread.operation.travelDistance = (int) dY;
			}
		}

	}
	
	protected class ControlThread extends Thread {
		public Operation operation = new Operation();
		private ControlThread controlThread;

		public ControlThread() {
			super("Robot control thread");
			setDaemon(true);
		}
		@Override
		
		
		public void run() {
			try {
				while (true) {
					Operation.Type op;
					int rotateBy, travelDist;
					synchronized (this) {
						op = this.operation.op;
						rotateBy = this.operation.rotateBy;
						travelDist = this.operation.travelDistance;
					}
					System.out.println("operation: " + op + " rotateBy: "
							 + rotateBy + " travelDist: " + travelDist);
					switch (op) {
					case DEFROTATE:
						if (rotateBy != 0) {
						brick.executeSync(new RobotCommand.Rotate(
								rotateBy, Math.abs(rotateBy)));
						}
						break;
					case DEFTRAVEL:
						 if (travelDist != 0) {
							brick.execute(new RobotCommand.Travel(
									travelDist / 3,
									Math.abs(travelDist) * 3 + 25));
						}
						break;
					case DESIDEWAYS:
						if (travelDist != 0) {
							brick.execute(new RobotCommand.TravelSideways(
									travelDist / 3,
									Math.abs(travelDist) * 3 + 25));
						}
						break;
					case DEBACK:
						if (travelDist != 0) {
							brick.execute(new RobotCommand.Travel(
									travelDist,
									travelDist));
						}
					default:
						break;
					}
					Thread.sleep(StrategyController.STRATEGY_TICK);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			finally {}
			
		}
	}

}
