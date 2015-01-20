package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew on 16/01/2015.
 */
public class KickPacket extends Packet {
    public static final byte ID = 'K';

    private byte power;

    public KickPacket(byte power) {
        this.power = power;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(SerialPort sendPort) throws SerialPortException {
        sendPort.writeByte(ID);
        sendPort.writeByte(power);
    }

    @Override
    public Packet readPacket(InputStream stream) {
        return new KickPacket((byte)0x0);
    }
}
