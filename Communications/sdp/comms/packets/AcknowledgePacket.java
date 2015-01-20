package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew Summers on 16/01/2015.
 */
public class AcknowledgePacket extends Packet {
    public final static byte ID = 'C';

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
        return new AcknowledgePacket();
    }
}
