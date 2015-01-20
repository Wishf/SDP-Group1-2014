package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew Summers on 16/01/2015.
 */
public abstract class Packet {
    public static final byte ID = 0x00;

    public abstract byte getID();

    public abstract void writePacket(SerialPort sendPort) throws SerialPortException;

    public abstract Packet readPacket(InputStream stream);
}
