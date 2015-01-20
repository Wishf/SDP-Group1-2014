package sdp.comms;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import sdp.comms.packets.Packet;

import java.util.Queue;

/**
 * Created by Matthew on 16/01/2015.
 */
public class RadioController implements SerialPortEventListener {
    private Queue<Packet> packets;
    private SerialPort parent;

    public RadioController(Queue<Packet> packets, SerialPort parent) {
        this.packets = packets;
        this.parent = parent;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // Message received
        if(event.isRXCHAR()) {
            if(event.getEventValue() >= 9) {
                String val = null;
                try {
                    val = parent.readString();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                if (val != null) {
                    System.out.println(val);
                }
            }
        }
    }
}
