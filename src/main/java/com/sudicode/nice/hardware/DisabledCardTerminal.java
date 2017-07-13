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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Card connect(String s) throws CardException {
        return null;
    }

    @Override
    public boolean isCardPresent() throws CardException {
        return false;
    }

    @Override
    public boolean waitForCardPresent(long l) throws CardException {
        return false;
    }

    @Override
    public boolean waitForCardAbsent(long l) throws CardException {
        return false;
    }

}
