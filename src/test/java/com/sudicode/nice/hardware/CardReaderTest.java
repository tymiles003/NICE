package com.sudicode.nice.hardware;

import org.junit.Test;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CardReader}.
 */
public class CardReaderTest {

    @Test
    public void testFailure() throws Exception {
        ResponseAPDU failure = new ResponseAPDU(new byte[]{(byte) 0xBE, (byte) 0xEF, (byte) 0x64, (byte) 0x00});
        CardReader cr = new CardReader(deviceThatReturns(failure));
        try {
            cr.readUID();
            fail("Expected CardException");
        } catch (CardException expected) {
            assertEquals("Command failed (SW=0x6400)", expected.getMessage());
        }
    }

    @Test
    public void testReadUID() throws Exception {
        ResponseAPDU response = new ResponseAPDU(new byte[]{(byte) 0xBE, (byte) 0xEF, (byte) 0x90, (byte) 0x00});
        CardReader cr = new CardReader(deviceThatReturns(response));
        assertEquals("BEEF", cr.readUID());
    }

    /**
     * Generate a {@link Device} that returns the given {@link ResponseAPDU} when
     * {@link Device#sendCommand(CommandAPDU)} is called.
     *
     * @param responseAPDU The {@link ResponseAPDU}
     * @return The {@link Device}
     */
    private Device deviceThatReturns(ResponseAPDU responseAPDU) {
        try {
            Device device = mock(Device.class);
            when(device.sendCommand(any())).thenReturn(responseAPDU);
            return device;
        } catch (CardException e) {
            throw new RuntimeException("Failed to create mock Device", e);
        }
    }

}
