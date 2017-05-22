package com.sudicode.nice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Smart card reader.
 */
public class CardReader {

    private static final Logger LOG = LoggerFactory.getLogger(CardReader.class);

    private final String deviceName;

    /**
     * Construct a new {@link CardReader}.
     *
     * @param deviceName Name of the USB device (e.g. "ACR122")
     */
    public CardReader(final String deviceName) {
        this.deviceName = deviceName;
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
     * Send a command APDU.
     *
     * @param commandAPDU The command APDU to send
     * @return Data portion of the response APDU
     * @throws CardException if command fails
     */
    private String send(byte[] commandAPDU) throws CardException {
        // Get the list of available terminals
        TerminalFactory tFactory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = tFactory.terminals().list();
        LOG.info("Available terminals: {}", terminals);
        CardTerminal terminal = terminals.stream()
                .filter(t -> t.getName().contains(deviceName))
                .findFirst()
                .orElseThrow(() -> new CardException(deviceName + " not found"));

        // Establish a connection with the card
        LOG.info("Waiting for a card.");
        terminal.waitForCardPresent(0);
        Card card = terminal.connect("T=1");
        CardChannel channel = card.getBasicChannel();

        // Initialize buffers
        byte[] buf = new byte[258];
        ByteBuffer bufCmd = ByteBuffer.wrap(commandAPDU);
        ByteBuffer bufResp = ByteBuffer.wrap(buf);
        StringBuilder responseAPDU = new StringBuilder();

        // Send command APDU, retrieve response APDU
        int output = channel.transmit(bufCmd, bufResp); // output = The length of the received response APDU
        for (int i = 0; i < output; i++) {
            responseAPDU.append(String.format("%02X", buf[i])); // The result is formatted as a hexadecimal integer
        }

        // Validate response APDU
        int digits = responseAPDU.length();
        if (digits < 4) {
            throw new CardException(String.format("Response APDU is too short (%d hexadecimal digits)", digits));
        }
        String data = responseAPDU.substring(0, digits - 4);
        String sw1 = responseAPDU.substring(digits - 4, digits - 2);
        String sw2 = responseAPDU.substring(digits - 2, digits);
        if (!sw1.equals("90") || !sw2.equals("00")) {
            throw new CardException(String.format("Command failed (SW1=0x%s, SW2=0x%s)", sw1, sw2));
        } else {
            return data;
        }
    }

    public static void main(String[] args) {
        try {
            LOG.info("The UID of your card is {}", new CardReader("ACR122").readUID());
        } catch (CardException e) {
            e.printStackTrace();
        }
    }

}
