package com.sudicode.nice.ui;

import com.google.inject.Inject;
import com.sudicode.nice.dao.Students;
import com.sudicode.nice.model.Student;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Generates JavaFX dialog boxes.
 */
public class Dialogs {

    private static final Logger log = LoggerFactory.getLogger(Dialogs.class);

    private final Students students;

    /**
     * Construct a new {@link Dialogs}.
     *
     * @param students The {@link Students} instance.
     */
    @Inject
    public Dialogs(Students students) {
        this.students = students;
    }

    /**
     * Adds a new student.
     *
     * @param studentId The new student ID
     */
    public void showNewStudentDialog(String studentId) {
        // Ask the instructor if they wish to add a new student.
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Student Not Found");
        confirm.setHeaderText("Student was not found in database.");
        confirm.setContentText("Register the student?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        // Create the custom dialog.
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("New Student");
        dialog.setHeaderText("Register the new student.");

        // Set the button types.
        ButtonType registerButton = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButton, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField middleName = new TextField();
        middleName.setPromptText("Middle Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField email = new TextField();
        email.setPromptText("Email");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Middle Name:"), 0, 1);
        grid.add(middleName, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastName, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first name field by default.
        Platform.runLater(firstName::requestFocus);

        // Convert the result to a student when the register button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButton) {
                Student s = students.newInstance();
                s.setStudentId(studentId);
                s.setFirstName(firstName.getText());
                s.setMiddleName(middleName.getText());
                s.setLastName(lastName.getText());
                s.setEmail(email.getText());
                return s;
            }
            return null;
        });

        // If instructor completes the registration form, add the student.
        dialog.showAndWait().ifPresent(s -> {
            try {
                s.save();
            } catch (SQLException e) {
                log.error("Caught exception while registering student.", e);
                showExceptionDialog(e);
            }
        });
    }

    /**
     * Show an exception dialog.
     *
     * @param e The {@link Exception} to encapsulate
     */
    public void showExceptionDialog(Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Critical Error");
        alert.setHeaderText("An error has occurred.");
        alert.setContentText("Click \"Show Details\" for the stack trace.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

}
