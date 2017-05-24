package com.sudicode.nice.hardware;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * Device which facilitates communication via APDUs (ISO/IEC 7816-4).
 */
public interface Device {

    /**
     * Send a {@link CommandAPDU} and return the response.
     *
     * @param commandAPDU The {@link CommandAPDU} to send
     * @return Response APDU
     * @throws CardException if command fails
     */
    ResponseAPDU sendCommand(CommandAPDU commandAPDU) throws CardException;

}
