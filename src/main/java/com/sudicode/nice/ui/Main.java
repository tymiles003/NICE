package com.sudicode.nice.ui;

import com.diffplug.common.base.DurianPlugins;
import com.diffplug.common.base.Errors;
import com.sudicode.nice.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.Assert;

/**
 * Main class.
 */
public class Main extends Application {

    public static void main(String[] args) {
        // Ensure that environment variables are set
        Assert.assertNotNull("Please set DB_USER in your environment variables", Constants.DB_USER);
        Assert.assertNotNull("Please set DB_PW in your environment variables", Constants.DB_PW);
        Assert.assertNotNull("Please set DB_SERVER in your environment variables", Constants.DB_SERVER);

        // Set default exception handling routine
        DurianPlugins.register(Errors.Plugins.Dialog.class, e -> Platform.runLater(() -> DialogFactory.showThrowableDialog(e)));

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
