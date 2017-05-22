package com.sudicode.nice.hardware;

import javax.smartcardio.CardException;

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
        return send(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
    }

    /**
     * Send a command APDU, then validate and return the data portion of the response.
     *
     * @param commandAPDU The command APDU to send
     * @return Data portion of the response APDU
     * @throws CardException if command fails
     */
    private String send(final byte[] commandAPDU) throws CardException {
        String response = device.sendCommand(commandAPDU);
        int digits = response.length();
        if (digits < 4) {
            throw new CardException(String.format("Response APDU is too short (%d hexadecimal digits)", digits));
        }
        String data = response.substring(0, digits - 4);
        String sw1 = response.substring(digits - 4, digits - 2);
        String sw2 = response.substring(digits - 2, digits);
        if (!sw1.equals("90") || !sw2.equals("00")) {
            throw new CardException(String.format("Command failed (SW1=0x%s, SW2=0x%s)", sw1, sw2));
        } else {
            return data;
        }
    }

}
