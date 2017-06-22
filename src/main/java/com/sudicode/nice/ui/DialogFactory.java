package com.sudicode.nice.ui;

import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.Student;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.IntegerValidator;

import javax.smartcardio.CardException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Generates JavaFX dialog boxes.
 */
public class DialogFactory {

    private static final IntegerValidator intValidator = IntegerValidator.getInstance();

    /**
     * Illegal.
     */
    private DialogFactory() {
    }

    /**
     * Shows a {@link Dialog} which allows the instructor to add or modify a {@link Student}.
     *
     * @param student The {@link Student} instance to add or modify
     * @return The result of {@link Dialog#showAndWait()}
     */
    public static Optional<Student> showStudentDialog(Student student) {
        // Create the custom dialog.
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Student Information");
        dialog.setHeaderText("Please provide student's information.");

        // Set the button types.
        ButtonType okButtonType = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fNameField = new TextField(student.getFirstName());
        fNameField.setPromptText("First Name");
        TextField mNameField = new TextField(student.getMiddleName());
        mNameField.setPromptText("Middle Name");
        TextField lNameField = new TextField(student.getLastName());
        lNameField.setPromptText("Last Name");
        TextField emailField = new TextField(student.getEmail());
        emailField.setPromptText("Email");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(fNameField, 1, 0);
        grid.add(new Label("Middle Name:"), 0, 1);
        grid.add(mNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first name field by default.
        Platform.runLater(fNameField::requestFocus);

        // Convert the result to a student when the register button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                student.setFirstName(fNameField.getText());
                student.setMiddleName(mNameField.getText());
                student.setLastName(lNameField.getText());
                student.setEmail(emailField.getText());
                return student;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Shows a {@link Dialog} which allows the instructor to add or modify a {@link Course}.
     *
     * @param course The {@link Course} instance to add or modify
     * @return The result of {@link Dialog#showAndWait()}
     */
    public static Optional<Course> showCourseDialog(Course course) {
        // Create the custom dialog.
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Course Information");
        dialog.setHeaderText("Please provide course information.");

        // Set the button types.
        ButtonType okButtonType = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField crnField = new TextField(course.getCrn() != 0 ? String.valueOf(course.getCrn()) : "");
        crnField.setPromptText("CRN");
        TextField nameField = new TextField(course.getName());
        nameField.setPromptText("Course Name");
        TextField numField = new TextField(course.getNumber());
        numField.setPromptText("Course Number");
        TextField sectField = new TextField(course.getSection() != 0 ? String.valueOf(course.getSection()) : "");
        sectField.setPromptText("Course Section");

        grid.add(new Label("CRN:"), 0, 0);
        grid.add(crnField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Course Number:"), 0, 2);
        grid.add(numField, 1, 2);
        grid.add(new Label("Course Section:"), 0, 3);
        grid.add(sectField, 1, 3);

        // Do some validation.
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        ChangeListener<String> validator = (x, y, z) -> {
            boolean disable = intValidator.validate(crnField.getText()) == null || intValidator.validate(sectField.getText()) == null;
            okButton.setDisable(disable);
        };
        validator.changed(null, null, null);
        crnField.textProperty().addListener(validator);
        sectField.textProperty().addListener(validator);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the crn field by default.
        Platform.runLater(crnField::requestFocus);

        // Convert the result to a student when the register button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                course.setCrn(Integer.parseInt(crnField.getText()));
                course.setName(nameField.getText());
                course.setNumber(numField.getText());
                course.setSection(Integer.parseInt(sectField.getText()));
                return course;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Show an exception dialog.
     *
     * @param e The {@link Exception} to display
     */
    public static void showExceptionDialog(Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Critical Error");
        if (e instanceof SQLException) {
            alert.setHeaderText("A database error has occurred.");
        } else if (e instanceof CardException) {
            alert.setHeaderText("A card reader error has occurred.");
        } else {
            alert.setHeaderText("An error has occurred.");
        }
        alert.setContentText(StringUtils.defaultString(e.getMessage(), "Click \"Show Details\" for the stack trace."));

        TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(e));
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
