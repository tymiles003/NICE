package com.sudicode.nice.hardware;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

/**
 * Used if no card terminal is available.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Null_Object_pattern">Null Object pattern</a>
 */
public class DisabledCardTerminal extends CardTerminal {

    private static String MESSAGE = "No card reader found, card reading has been disabled!";

    @Override
    public String getName() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public Card connect(String s) throws CardException {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public boolean isCardPresent() throws CardException {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public boolean waitForCardPresent(long l) throws CardException {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public boolean waitForCardAbsent(long l) throws CardException {
        throw new UnsupportedOperationException(MESSAGE);
    }

}
