package sdp.comms;

import sdp.comms.packets.MotionCompletePacket;
import sdp.comms.packets.Packet;

/**
 * Created by Matthew on 06/02/2015.
 */
public abstract class BaseRobotController implements PacketListener {
    @Override
    public void packetArrived(Packet p) {
        if(p instanceof MotionCompletePacket){
            onMotionComplete();
        }
    }
    public abstract void onMotionComplete();
}
