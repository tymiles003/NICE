package com.sudicode.nice.hardware;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.xml.bind.DatatypeConverter;

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
     * @throws CardException if command fails
     */
    public String readUID() throws CardException {
        CommandAPDU command = new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        return stringify(device.sendCommand(command));
    }

    /**
     * Return the data portion of a {@link ResponseAPDU}, formatted as a hex string.
     *
     * @param responseAPDU A {@link ResponseAPDU}
     * @return Response data formatted as a hex string
     * @throws CardException if the command processing status is not equal to 0x9000
     */
    private String stringify(final ResponseAPDU responseAPDU) throws CardException {
        int sw = responseAPDU.getSW();
        if (sw != 0x9000) {
            throw new CardException(String.format("Command failed (SW=0x%04X)", sw));
        } else {
            return DatatypeConverter.printHexBinary(responseAPDU.getData());
        }
    }

}
