package sdp.sdp9.strategy;

import sdp.sdp9.comms.BrickCommServer;
import sdp.sdp9.comms.RobotCommand;
import sdp.sdp9.strategy.interfaces.Strategy;
import sdp.sdp9.world.oldmodel.WorldState;

public class LateNightStrategy implements Strategy {

	
	private BrickCommServer brick;
	private ControlThread controlThread;

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
		// TODO Auto-generated method stub
		synchronized (this.controlThread) {
			this.controlThread.operation.op = Operation.Type.DO_NOTHING;
		}

	}
	
	protected class ControlThread extends Thread {
		public Operation operation = new Operation();
		private ControlThread controlThread;

		public ControlThread() {
			
			@Override
			public void run() {
				try {
					while (true) {
						int travelDist, rotateBy, travelSpeed;
						Operation.Type op;
						double radius;
						synchronized (this) {
							op = this.operation.op;
							rotateBy = this.operation.rotateBy;
							travelDist = this.operation.travelDistance;
							travelSpeed = this.operation.travelSpeed;
							radius = this.operation.radius;
						}


						switch (op) {
						case DO_NOTHING:

							break;
						case ATKKICK:
							if (System.currentTimeMillis() - lastKickerEventTime > 500) {
								brick.execute(new RobotCommand.Kick(100));
								ballCaughtAttacker = false;
								lastKickerEventTime = System.currentTimeMillis();
							}
							break;
						}
					}
				} finally {
					
				}
			}
		}
	}

}
