package sdp.comms;

import sdp.comms.packets.*;
import sdp.util.DriveDirection;

public class Main {

    public static void main(String[] args) {
        Radio rad = new Radio("COM4");
        rad.start();
        rad.sendPacket(new ActivatePacket());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Packet pack = rad.receivePacket();
        if(pack instanceof AcknowledgePacket){
            System.out.println("Activation acknowledged");
        }else if (pack == null) {
            System.out.println("Failed.");
        }
        rad.sendPacket(new DrivePacket(
                (byte)255, DriveDirection.FORWARD,
                (byte)255, DriveDirection.FORWARD,
                (byte)255, DriveDirection.FORWARD)
        );
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pack = rad.receivePacket();
        if(pack instanceof AcknowledgePacket){
            System.out.println("Drive acknowledged");
        } else if (pack == null) {
            System.out.println("Failed.");
        }
        rad.stop();

    }
}
