package com.sudicode.nice.hardware;

import com.google.inject.Inject;

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
    @Inject
    public CardReader(final Device device) {
        this.device = device;
    }

    /**
     * Access the card reader and wait for a card. Once the card is present, read its UID.
     *
     * @return The UID
     * @throws CardException if the operation failed
     */
    public String readUID() throws CardException {
        CommandAPDU command = new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        ResponseAPDU response = device.sendCommand(command);

        int sw = response.getSW();
        if (sw != 0x9000) {
            throw new CardException(String.format("Command failed (SW=0x%04X)", sw));
        } else {
            return DatatypeConverter.printHexBinary(response.getData());
        }
    }

}
