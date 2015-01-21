package sdp.gui;

import sdp.comms.Radio;

/**
 * Created by conrad on 21/01/15.
 */
public class ArduinoWrapper implements Runnable {

    private Thread thread;

    public void start() {
        if(thread == null) {
            thread = new Thread(this, "ArduinoWrapper");
            thread.start();
        }
    }

    @Override
    public void run() {
        Radio rad = new Radio("COM3");
        rad.start();
        rad.stop();

    }
}
