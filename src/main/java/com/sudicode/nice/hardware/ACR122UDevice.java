package com.sudicode.nice.hardware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
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
    public ResponseAPDU sendCommand(final CommandAPDU commandAPDU) throws CardException {
        // Establish a connection with the card
        log.info("Waiting for a card");
        terminal.waitForCardPresent(0);
        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();

        // Send command APDU, retrieve response APDU
        return channel.transmit(commandAPDU);
    }

}
