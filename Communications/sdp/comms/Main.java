package sdp.comms;

import sdp.comms.packets.ActivatePacket;
import sdp.comms.packets.DeactivatePacket;

public class Main {

    public static void main(String[] args) {
        Radio rad = new Radio("COM3");
        rad.start();
        rad.sendPacket(new ActivatePacket());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rad.sendPacket(new DeactivatePacket());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rad.stop();

    }
}
