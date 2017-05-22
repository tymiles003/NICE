package com.sudicode.nice.hardware;

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
 * ACR122U USB NFC Reader device.
 */
public class ACR122UDevice implements Device {

    private static final String DEVICE_NAME = "ACR122";

    private static final Logger log = LoggerFactory.getLogger(ACR122UDevice.class);
    private static final TerminalFactory terminalFactory = TerminalFactory.getDefault();

    private final CardTerminal terminal;

    /**
     * Construct a new {@link ACR122UDevice}.
     *
     * @throws CardException if the device cannot be found
     */
    public ACR122UDevice() throws CardException {
        List<CardTerminal> terminals = terminalFactory.terminals().list();
        log.info("Available terminals: {}", terminals);
        terminal = terminals.stream()
                .filter(t -> t.getName().contains(DEVICE_NAME))
                .findFirst()
                .orElseThrow(() -> new CardException(DEVICE_NAME + " not found"));
        log.info("{} found, commands will be sent to {}", DEVICE_NAME, terminal);

    }

    @Override
    public String sendCommand(final byte[] commandAPDU) throws CardException {
        // Establish a connection with the card
        log.info("Waiting for a card");
        terminal.waitForCardPresent(0);
        Card card = terminal.connect("*");
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
        return responseAPDU.toString();
    }

}
