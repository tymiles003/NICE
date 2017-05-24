package com.sudicode.nice.ui;

import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.hardware.CardTerminalDevice;
import com.sudicode.nice.hardware.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;

/**
 * Sample usage of {@link CardReader}.
 */
public class CardReaderControl {

    private static final Logger log = LoggerFactory.getLogger(CardReaderControl.class);

    public static void main(String[] args) {
        try {
            TerminalFactory terminalFactory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = terminalFactory.terminals().list();
            log.info("Available terminals: {}", terminals);
            CardTerminal cardTerminal = terminals.get(0);
            log.info("Commands will be sent to {}", cardTerminal.getName());

            Device device = new CardTerminalDevice(cardTerminal);
            CardReader cr = new CardReader(device);
            log.info("UID: {}", cr.readUID());
        } catch (CardException e) {
            log.error("Reading failed", e);
        }
    }

}
