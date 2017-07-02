package com.sudicode.nice.hardware;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.nio.ByteBuffer;

/**
 * Smart card reader.
 */
public class CardReader {

    private final Device device;

    /**
     * Construct a new {@link CardReader}.
     *
     * @param device The {@link Device} to use.
     */
    public CardReader(final Device device) {
        this.device = device;
    }

    /**
     * Access the card reader and wait for a card. Once the card is present, read its UID.
     *
     * @return The UID
     * @throws CardException if the operation failed
     */
    public int readUID() throws CardException {
        CommandAPDU command = new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        ResponseAPDU response = device.sendCommand(command);

        int sw = response.getSW();
        if (sw != 0x9000) {
            throw new CardException(String.format("Command failed (SW=0x%04X)", sw));
        } else {
            byte[] data = response.getData();
            if (data.length < 4) {
                // Zerofill necessary
                byte[] newArray = new byte[4];
                System.arraycopy(data, 0, newArray, 4 - data.length, data.length);
                data = newArray;
            }
            return ByteBuffer.wrap(data).getInt();
        }
    }

}
