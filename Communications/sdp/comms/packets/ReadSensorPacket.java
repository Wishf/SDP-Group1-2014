package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew on 16/01/2015.
 */
public class ReadSensorPacket extends Packet {
    public static final byte ID = 'R';

    private byte sensorID;

    public ReadSensorPacket(byte sensorID) {
        this.sensorID = sensorID;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(SerialPort sendPort) throws SerialPortException {
        sendPort.writeByte(ID);
        sendPort.writeByte(sensorID);
    }

    @Override
    public Packet readPacket(InputStream stream) {
        return new ReadSensorPacket((byte)0x0);
    }
}
