package sdp.control;

import sdp.comms.PacketListener;
import sdp.comms.Radio;
import sdp.comms.packets.*;

/**
 * Created by Matthew on 06/02/2015.
 */
public abstract class BaseRobotController implements PacketListener {
    private boolean catcherEngaged;
    private MotionQueue motions;
    private Radio radio;

    public BaseRobotController(Radio radio, boolean initialCatcher) {
        catcherEngaged = initialCatcher;
        motions = new MotionQueue(16);
        this.radio = radio;
        radio.addListener(this);
    }

    public void enqueueMotion(Maneuver maneuver) {
        if(motions.enqueue(maneuver)) {
            EnqueueMotionPacket pkt = new EnqueueMotionPacket(
                    maneuver.getMotorPower(0),
                    maneuver.getMotorDirection(0),
                    maneuver.getMotorPower(1),
                    maneuver.getMotorDirection(1),
                    maneuver.getMotorPower(2),
                    maneuver.getMotorDirection(2),
                    maneuver.getDuration()
            );
            radio.sendPacket(pkt);
        }

    }

    public void popMotion() {
        motions.pop();
        radio.sendPacket(new PopQueuePacket());
    }

    public void clearMotions(){
        motions.clear();
        radio.sendPacket(new ClearQueuePacket());
    }

    @Override
    public void packetArrived(Packet p) {
        if(p instanceof MotionCompletePacket){
            onMotionComplete();
            motions.pop();
        } else if(p instanceof CatcherStateToggledPacket) {
            catcherEngaged = !catcherEngaged;
        }
    }

    public boolean getCatcherState(){
        return catcherEngaged;
    }

    public abstract void onMotionComplete();
}
