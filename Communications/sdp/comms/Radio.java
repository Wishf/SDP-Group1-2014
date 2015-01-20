package sdp.comms;

import jssc.SerialPort;
import jssc.SerialPortException;
import sdp.comms.packets.Packet;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Matthew on 16/01/2015.
 */
public class Radio {
    private SerialPort port;
    private Queue<Packet> packetQueue;

    public Radio(String portName){
        port = new SerialPort(portName);
        packetQueue = new LinkedList<Packet>();
    }

    public void start(){
        try {
            port.openPort();
            port.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            port.setEventsMask(SerialPort.MASK_RXCHAR);
            port.addEventListener(new RadioController(packetQueue, port));
            Thread.sleep(5000);
        }
        catch(SerialPortException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try{
            port.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet) {
        packetQueue.add(packet);
        Packet next = packetQueue.poll();
        if(next != null) {
            System.out.println("Sent packet");
            try {
                next.writePacket(port);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
