package com.sudicode.nice.ui;

import com.sudicode.nice.hardware.ACR122UDevice;
import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.hardware.Device;

import javax.smartcardio.CardException;

/**
 * Sample usage of {@link CardReader}.
 */
public class CardReaderControl {

    public static void main(String[] args) {
        try {
            Device device = new ACR122UDevice();
            CardReader cr = new CardReader(device);
            System.out.println("UID: " + cr.readUID());
        } catch (CardException e) {
            e.printStackTrace();
        }
    }

}
