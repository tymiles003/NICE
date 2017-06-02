package com.sudicode.nice.ui;

import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.hardware.CardTerminalDevice;
import com.sudicode.nice.hardware.Device;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;

/**
 * Main class.
 */
public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws CardException {
        // Initialize hardware
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = terminalFactory.terminals().list();
        log.info("Available terminals: {}", terminals);
        CardTerminal cardTerminal = terminals.get(0);
        log.info("Commands will be sent to {}", cardTerminal.getName());
        Device device = new CardTerminalDevice(cardTerminal);
        CardReader cr = new CardReader(device);

        // Initialize GUI
        primaryStage.setTitle("NICE Attendance Taker");
        Text text = new Text("Please tap your card...");
        text.setFont(Font.font(24));
        StackPane root = new StackPane();
        root.getChildren().add(text);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

        // Listen for cards
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    String uid = cr.readUID();
                    Platform.runLater(() -> text.setText("ID: " + uid));
                    cardTerminal.waitForCardAbsent(0);
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

}
