package sdp.gui;

import sdp.comms.Radio;
import sdp.comms.packets.*;

/**
 * Created by conrad on 27/01/15.
 */
public class SingletonRadio {
    private static Radio rad;

    public SingletonRadio() {
        if(rad == null) {
            rad = new Radio("/dev/tty.usbmodem000001");
            rad.start();
            rad.sendPacket(new ActivatePacket());
        }
    }

    public void sendPacket(Packet packet) {
        rad.sendPacket(packet);
    }
}
