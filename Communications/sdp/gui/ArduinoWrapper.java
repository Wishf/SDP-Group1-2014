package sdp.gui;

import sdp.comms.Radio;
import sdp.comms.packets.*;

import java.util.ArrayList;

/**
 * Created by conrad on 21/01/15.
 */
public class ArduinoWrapper implements Runnable {

    private Thread thread;
    private SingletonRadio rad;
    private SingletonDebugWindow debugWindow;
    private ArrayList<String> commandQueue;
    private int kickPower;
    private static int timePerCm = 50;

    public ArduinoWrapper() {
        commandQueue = new ArrayList<String>();
        debugWindow = new SingletonDebugWindow();
        kickPower = 255;
    }

    public void setKickPower(int power) {
        kickPower = power;
        debugWindow.addDebugInfo("Setting KP to " + Integer.toString(kickPower));
    }

    public void sendCommand(String comm) {
        commandQueue.add(comm);
        System.out.println("Got command "+comm);
    }

    private String getNextCommand() {
        String command = null;

        while(command == null) {
            if(commandQueue.size() > 0 ) {
                command = commandQueue.remove(0);
            } else {
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return command;
    }

    private boolean performNextCommand() {

        String command = getNextCommand();

        if(command.equals("Quit")) {
            return false;
        } else if(command.equals("Start")) {
            //rad.start();
            rad.sendPacket(new ActivatePacket());
        } else if(command.equals("Stop")) {
            //rad.stop();
        } else if(command.equals("50cm Forward")) {
            goForward(42);
        } else if(command.equals("10cm Forward")) {
            goForward(15);
        } else if(command.equals("20cm Backward")) {
            //goForward(-21);
            goForward(-42);
        } else if(command.equals("Activate")) {
            rad.sendPacket(new ActivatePacket());
        } else if(command.equals("Deactivate")) {
            rad.sendPacket(new DeactivatePacket());
        } else if(command.equals("Kick")) {
            debugWindow.addDebugInfo("Kicking");
            rad.sendPacket(new KickPacket((byte) kickPower));
        } else if(command.equals("Dance Like a Wee Lassie")) {
            danceLassie();
        }

        return true;
    }

    private void danceLassie() {
        byte stop = 0;
        rad.sendPacket(new DrivePacket((byte)200, (byte) 1, (byte)200, (byte)0));
        PacketLifeTime plt = new PacketLifeTime(new DrivePacket(stop, stop, stop, stop), 5000);
        plt.start();
    }

    private void goForward(int cm) {
        byte speed_l, speed_r;
        byte dir_l, dir_r;
        byte stop = (byte) 0;
        int time = Math.abs(cm*timePerCm);
        if(cm < 0) {
            // Backward
            speed_l = (byte) 188; //188
            speed_r = (byte) 188;
            dir_l = 1;
            dir_r = 1;
        } else {
            // Forward
            speed_l = (byte) 188;
            speed_r = (byte) 188; //163
            dir_l = 0;
            dir_r = 0;


        }
        debugWindow.addDebugInfo("Going " + Integer.toString(cm) + "cm forward. Will take " + Integer.toString(time) + "ms");
        rad.sendPacket(new DrivePacket(speed_l, dir_l, speed_r, dir_r));
        PacketLifeTime plt = new PacketLifeTime(new DrivePacket(stop, stop, stop, stop), time);
        plt.start();
        debugWindow.addDebugInfo("Done.");
    }
    public void start() {
        if(thread == null) {
            thread = new Thread(this, "ArduinoWrapper");
            thread.start();
        }
    }

    @Override
    public void run() {
        rad = new SingletonRadio();
        debugWindow.addDebugInfo("Started Arduino");

        boolean go = true;

        while(go) {
            go = performNextCommand();
        }

        debugWindow.addDebugInfo("Stopping Arduino and terminating...");
        System.exit(0);
    }
}
