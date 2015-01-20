package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.InputStream;

/**
 * Created by Matthew on 16/01/2015.
 */
public class DrivePacket extends Packet {
    public final static byte ID = 'M';

    private byte[] motorPowers;

    public DrivePacket(byte motor1Power, byte motor2Power, byte motor3Power, byte motor4Power) {
        motorPowers = new byte[4];
        motorPowers[0] = motor1Power;
        motorPowers[1] = motor2Power;
        motorPowers[2] = motor3Power;
        motorPowers[3] = motor4Power;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(SerialPort sendPort) throws SerialPortException {
        sendPort.writeByte(ID);
        sendPort.writeBytes(motorPowers);
    }

    @Override
    public Packet readPacket(InputStream stream) {
        return new DrivePacket((byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0);
    }
}
