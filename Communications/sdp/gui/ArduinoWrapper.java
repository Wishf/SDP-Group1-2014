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
            goForward(-21);
        } else if(command.equals("Activate")) {
            rad.sendPacket(new ActivatePacket());
        } else if(command.equals("Deactivate")) {
            rad.sendPacket(new DeactivatePacket());
        } else if(command.equals("Kick")) {
            debugWindow.addDebugInfo("Kicking");
            rad.sendPacket(new KickPacket((byte) kickPower));
            /*try {
                thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

        return true;
    }

    private void goForward(int cm) {
        byte speed_l, speed_r;
        byte stop = (byte) 128;
        int time = Math.abs(cm*timePerCm);
        if(cm < 0) {
            speed_l = 61;
            speed_r = 64;
        } else {
            speed_l = (byte) 128+64;
            speed_r = (byte) 128+61;
        }
        debugWindow.addDebugInfo("Going " + Integer.toString(cm) + "cm forward. Will take " + Integer.toString(time) + "ms");
        rad.sendPacket(new DrivePacket((byte) speed_l, speed_r, stop, stop));
        /*try {
            thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        PacketLifeTime plt = new PacketLifeTime(new DrivePacket(stop, stop, stop, stop), time);
        plt.start();
        //rad.sendPacket(new DrivePacket(stop, stop, stop, stop));
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
        //rad = new Radio("COM1");
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
