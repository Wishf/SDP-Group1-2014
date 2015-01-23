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
    private SingletonDebugWindow debugWindow;
    private ArrayList<String> commandQueue;
    private static int timePerCm = 250;

    public ArduinoWrapper() {
        commandQueue = new ArrayList<String>();
        debugWindow = new SingletonDebugWindow();
    }


    public void sendCommand(String comm) {
        commandQueue.add(comm);
        System.out.println("Got command "+comm);
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
            debugWindow.addDebugInfo("Kicking");
            rad.sendPacket(new KickPacket((byte) 255));
            try {
                thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void goForward(int cm) {
        byte speed;
        byte stop = (byte) 128;
        int time = Math.abs(cm*timePerCm);
        if(cm < 0) {
            speed = 0;
        } else {
            speed = (byte) 255;
        }
        debugWindow.addDebugInfo("Going " + Integer.toString(cm) + "cm forward. Will take " + Integer.toString(time) +"ms");
        rad.sendPacket(new DrivePacket(speed, speed, stop, stop));
        try {
            thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rad.sendPacket(new DrivePacket(stop, stop, stop, stop));
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
        rad = new Radio("/dev/tty.usbmodem000001");
        rad.start();
        debugWindow.addDebugInfo("Started Arduino");

        boolean go = true;

        while(go) {
            go = performNextCommand();
        }

        debugWindow.addDebugInfo("Stopping Arduino and terminating...");
        rad.stop();
        System.exit(0);
    }
}
