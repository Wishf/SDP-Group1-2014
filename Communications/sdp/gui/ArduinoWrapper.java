package sdp.gui;

import sdp.comms.Radio;
import sdp.comms.packets.*;

import java.util.ArrayList;

/**
 * Created by conrad on 21/01/15.
 */
public class ArduinoWrapper implements Runnable {

    private Thread thread;
    private Radio rad;
    private SimpleGUI guiframe;
    private ArrayList<String> commandQueue;
    private static int timePerCm = 5;

    public ArduinoWrapper() {
        commandQueue = new ArrayList<String>();
    }

    public void addFrame(SimpleGUI pane) {
        guiframe = pane;
    }

    public void sendCommand(String comm) {
        commandQueue.add(comm);
    }

    private String getNextCommand() {
        String command = "";

        while(command.equals("")) {
            if(commandQueue.size() > 0 ) {
                command = commandQueue.remove(0);
            } else {
                command = "";
                try {
                    thread.sleep(200);
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
            rad.start();
        } else if(command.equals("Stop")) {
            rad.stop();
        } else if(command.equals("50cm Forward")) {
            goForward(50);
        } else if(command.equals("10cm Forward")) {
            goForward(10);
        } else if(command.equals("20cm Backward")) {
            goForward(-20);
        } else if(command.equals("Kick")) {
            rad.sendPacket(new KickPacket((byte) 5));
        }

        return true;
    }

    private void goForward(int cm) {
        byte speed = 100;
        int time = Math.abs(cm*timePerCm);
        if(cm < 0) {
            speed *= -1;
        }
        rad.sendPacket(new DrivePacket(speed, speed, (byte) 0, (byte) 0));
        try {
            thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rad.sendPacket(new DrivePacket((byte) 0, (byte) 0, (byte) 0, (byte) 0));
    }
    public void start() {
        if(thread == null) {
            thread = new Thread(this, "ArduinoWrapper");
            thread.start();
        }
    }

    @Override
    public void run() {
        rad = new Radio("COM3");
        rad.start();
        guiframe.addDebugInfo("Started Arduino");

        boolean go = true;

        while(go) {
            go = performNextCommand();
        }

        guiframe.addDebugInfo("Stopping Arduino and terminating...");
        rad.stop();
    }
}
