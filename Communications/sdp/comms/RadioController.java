package sdp.comms;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import sdp.comms.packets.Packet;
import sdp.gui.SingletonDebugWindow;

import java.util.Queue;

/**
 * Created by Matthew on 16/01/2015.
 */
public class RadioController implements SerialPortEventListener {
    private Queue<Packet> packets;
    private SerialPort parent;
    private SingletonDebugWindow debugWindow;

    public RadioController(Queue<Packet> packets, SerialPort parent) {
        this.packets = packets;
        this.parent = parent;
        debugWindow = new SingletonDebugWindow();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // Message received
        if(event.isRXCHAR()) {
            if(event.getEventValue() >= 1) {
                String val = null;
                try {
                    val = parent.readString();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                if (val != null) {
                    debugWindow.addDebugInfo(val);
                }
            }
        }
    }
}
