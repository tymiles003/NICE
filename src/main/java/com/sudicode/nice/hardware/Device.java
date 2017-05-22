package com.sudicode.nice.hardware;

import javax.smartcardio.CardException;

/**
 * Device which facilitates communication via APDUs (ISO/IEC 7816-4).
 */
public interface Device {

    /**
     * Send a command APDU and return the response.
     *
     * @param commandAPDU The command APDU to send
     * @return Response APDU
     * @throws CardException if command fails
     */
    String sendCommand(byte[] commandAPDU) throws CardException;

}
