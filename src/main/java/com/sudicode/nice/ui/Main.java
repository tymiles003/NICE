package com.sudicode.nice.ui;

import com.diffplug.common.base.DurianPlugins;
import com.diffplug.common.base.Errors.Plugins.Dialog;
import com.sudicode.nice.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class.
 */
public class Main extends Application {

    public static void main(String[] args) {
        // Set default exception handling routine
        DurianPlugins.register(Dialog.class, e -> Platform.runLater(() -> DialogFactory.showThrowableDialog(e)));

        // Run the application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AttendanceTaker.fxml"));
        Scene scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene.getStylesheets().add(Constants.STYLESHEET_URL);
        primaryStage.setTitle("NICE Attendance Taker");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Constants.ICON_URL));
        primaryStage.show();
    }

}
