package sdp.comms;

import sdp.comms.packets.CatcherStateToggledPacket;
import sdp.comms.packets.MotionCompletePacket;
import sdp.comms.packets.Packet;

/**
 * Created by Matthew on 06/02/2015.
 */
public abstract class BaseRobotController implements PacketListener {
    private boolean catcherEngaged;

    public BaseRobotController(boolean initialCatcher) {
        catcherEngaged = initialCatcher;
    }

    @Override
    public void packetArrived(Packet p) {
        if(p instanceof MotionCompletePacket){
            onMotionComplete();
        } else if(p instanceof CatcherStateToggledPacket) {
            catcherEngaged = !catcherEngaged;
        }
    }

    public boolean getCatcherState(){
        return catcherEngaged;
    }

    public abstract void onMotionComplete();
}
