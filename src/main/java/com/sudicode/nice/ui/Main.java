package com.sudicode.nice.ui;

import com.sudicode.nice.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        // Run the application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AttendanceTaker.fxml"));
        Scene scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setTitle("NICE Attendance Taker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
