package com.sudicode.nice.hardware;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * {@link Device} that uses a {@link CardTerminal} to transmit {@link CommandAPDU CommandAPDUs}.
 */
public class CardTerminalDevice implements Device {

    private static final Logger log = LoggerFactory.getLogger(CardTerminalDevice.class);

    private final CardTerminal cardTerminal;

    /**
     * Construct a new {@link CardTerminalDevice}.
     *
     * @param cardTerminal The {@link CardTerminal} to use
     */
    @Inject
    public CardTerminalDevice(final CardTerminal cardTerminal) {
        this.cardTerminal = cardTerminal;

    }

    @Override
    public ResponseAPDU sendCommand(final CommandAPDU commandAPDU) throws CardException {
        // Establish a connection with the card
        log.info("Waiting for a card");
        cardTerminal.waitForCardPresent(0);
        Card card = cardTerminal.connect("*");
        CardChannel channel = card.getBasicChannel();

        // Send command APDU, retrieve response APDU
        return channel.transmit(commandAPDU);
    }

}
