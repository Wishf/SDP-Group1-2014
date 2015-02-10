package sdp.sdp9.strategy;

import sdp.sdp9.comms.BrickCommServer;
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

	}
	
	protected class ControlThread extends Thread {
		public Operation operation = new Operation();
		private ControlThread controlThread;

		public ControlThread() {
		
		}
	}

}
