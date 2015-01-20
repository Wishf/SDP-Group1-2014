package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew Summers on 16/01/2015.
 */
public class SensorDataPacket extends Packet {
    public static final byte ID = 'S';

    private int data;

    public SensorDataPacket(int data) {
        this.data = data;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(SerialPort sendPort) throws SerialPortException {
        sendPort.writeByte(ID);
    }

    @Override
    public Packet readPacket(InputStream stream) {
        return new SensorDataPacket(0x0);
    }
}
