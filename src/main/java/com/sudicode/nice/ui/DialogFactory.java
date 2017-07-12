package com.sudicode.nice.ui;

import com.diffplug.common.base.Errors;
import com.sudicode.nice.Constants;
import com.sudicode.nice.Util;
import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.Student;
import com.sudicode.nice.hardware.CardReader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimeTextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.IntegerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Generates JavaFX dialog boxes.
 */
public class DialogFactory {

    private static final Logger log = LoggerFactory.getLogger(DialogFactory.class);
    private static final IntegerValidator intValidator = IntegerValidator.getInstance();

    /**
     * Illegal.
     */
    private DialogFactory() {
    }

    /**
     * Build add student dialog box.
     *
     * @return Dialog which asks the instructor if they wish to add a new student.
     */
    public static Alert getAddStudentDialog() {
        Alert alert = newAlert(AlertType.CONFIRMATION);
        alert.setTitle("Student Not Found");
        alert.setHeaderText("Student was not found in database.");
        alert.setContentText("Register the student?");
        return alert;
    }

    /**
     * Build enroll student dialog box.
     *
     * @param student The {@link Student}
     * @param course  The {@link Course}
     * @return Dialog which asks the instructor if they wish to enroll a student in a course.
     */
    public static Alert getEnrollStudentDialog(Student student, Course course) {
        Alert alert = newAlert(AlertType.CONFIRMATION);
        alert.setTitle("Student Not Enrolled");
        alert.setHeaderText(String.format("%s is not enrolled in %s.", student, course));
        alert.setContentText("Enroll the student?");
        return alert;
    }

    /**
     * Build delete course dialog box.
     *
     * @param course The {@link Course}
     * @return Dialog which asks the instructor if they wish to delete a course.
     */
    public static Alert getDeleteCourseDialog(Course course) {
        Alert alert = newAlert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(String.format("Really delete %s?", course));
        alert.setContentText("All enrolled students will be dropped!");
        return alert;
    }

    /**
     * Show a {@link Dialog} informing the instructor to select a {@link Course}.
     */
    public static void showNoCourseSelectedDialog() {
        Alert alert = newAlert(AlertType.INFORMATION);
        alert.setTitle("No Course Selected");
        alert.setHeaderText("Select a course.");
        alert.setContentText("You must select a course to perform this action.");
        alert.showAndWait();
    }

    /**
     * Build delete student dialog box.
     *
     * @param student The {@link Student}
     * @return Dialog which asks the instructor if they wish to delete a student.
     */
    public static Alert getDeleteStudentDialog(Student student) {
        Alert alert = newAlert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(String.format("Really delete %s?", student));
        alert.setContentText("This action is irreversible!");
        return alert;
    }

    /**
     * Shows dialog which confirms that an object has been updated.
     *
     * @param o The {@link Object}
     */
    public static void showObjectUpdatedDialog(Object o) {
        Alert alert = newAlert(AlertType.INFORMATION);
        alert.setTitle("Update Successful");
        alert.setHeaderText("Update Successful");
        alert.setContentText(String.format("Succesfully updated %s.", o));
        alert.show();
    }

    /**
     * Show dialog which confirms that an object has been deleted.
     *
     * @param o The {@link Object}
     */
    public static void showObjectDeletedDialog(Object o) {
        Alert alert = newAlert(AlertType.INFORMATION);
        alert.setTitle("Delete Successful");
        alert.setHeaderText("Delete Successful");
        alert.setContentText(String.format("Succesfully deleted %s.", o));
        alert.show();
    }

    /**
     * Show a dialog which requests the instructor to tap a student's card. If the dialog is closed before a card is
     * tapped, do nothing. When a card is tapped, close the dialog and then call the callback with the card's UID
     * as input.
     *
     * @param cardReader The {@link CardReader} to use
     * @param callback   A {@link Consumer} which is given the UID of the card
     */
    public static void showAsyncWaitForCardDialog(CardReader cardReader, Consumer<Integer> callback) {
        Dialog<Student> dialog = newDialog();
        dialog.setTitle("Tap Card");
        dialog.setHeaderText("Please tap the student's card.");
        dialog.setContentText("Waiting for card...");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> null);
        dialog.show();
        Util.submitBackgroundTask(Errors.dialog().wrap(() -> {
            int uid = cardReader.readUID();
            if (dialog.isShowing()) {
                Platform.runLater(dialog::close);
                Platform.runLater(() -> callback.accept(uid));
            }
        }));
    }

    /**
     * Shows a {@link Dialog} which allows the instructor to add or modify a {@link Student}.
     *
     * @param student The {@link Student} instance to add or modify
     * @return The result of {@link Dialog#showAndWait()}
     */
    public static Optional<Student> showStudentDialog(Student student) {
        // Create the custom dialog.
        Dialog<Student> dialog = newDialog();
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
        Dialog<Course> dialog = newDialog();
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

        TextField crnField = new TextField(course.getCrn() != null ? String.valueOf(course.getCrn()) : "");
        crnField.setPromptText("CRN");
        crnField.setDisable(course.getCrn() != null);
        TextField nameField = new TextField(course.getName());
        nameField.setPromptText("Course Name");
        TextField numField = new TextField(course.getNumber());
        numField.setPromptText("Course Number");
        TextField sectField = new TextField(course.getSection() != null ? String.valueOf(course.getSection()) : "");
        sectField.setPromptText("Course Section");

        LocalTimeTextField mondayFrom = new LocalTimeTextField();
        LocalTimeTextField mondayTo = new LocalTimeTextField();
        HBox mondayTimeRange = new HBox(mondayFrom, new Label("to:"), mondayTo);
        mondayTimeRange.setSpacing(10);
        mondayTimeRange.setAlignment(Pos.CENTER);

        CheckBox mondayCheckBox = new CheckBox("Monday");
        mondayCheckBox.setSelected(course.getMondayStart() != null && course.getMondayEnd() != null);
        mondayFrom.disableProperty().bind(mondayCheckBox.selectedProperty().not());
        mondayTo.disableProperty().bind(mondayCheckBox.selectedProperty().not());

        LocalTimeTextField tuesdayFrom = new LocalTimeTextField();
        LocalTimeTextField tuesdayTo = new LocalTimeTextField();
        HBox tuesdayTimeRange = new HBox(tuesdayFrom, new Label("to:"), tuesdayTo);
        tuesdayTimeRange.setSpacing(10);
        tuesdayTimeRange.setAlignment(Pos.CENTER);

        CheckBox tuesdayCheckBox = new CheckBox("Tuesday");
        tuesdayCheckBox.setSelected(course.getTuesdayStart() != null && course.getTuesdayEnd() != null);
        tuesdayFrom.disableProperty().bind(tuesdayCheckBox.selectedProperty().not());
        tuesdayTo.disableProperty().bind(tuesdayCheckBox.selectedProperty().not());

        LocalTimeTextField wednesdayFrom = new LocalTimeTextField();
        LocalTimeTextField wednesdayTo = new LocalTimeTextField();
        HBox wednesdayTimeRange = new HBox(wednesdayFrom, new Label("to:"), wednesdayTo);
        wednesdayTimeRange.setSpacing(10);
        wednesdayTimeRange.setAlignment(Pos.CENTER);

        CheckBox wednesdayCheckBox = new CheckBox("Wednesday");
        wednesdayCheckBox.setSelected(course.getWednesdayStart() != null && course.getWednesdayEnd() != null);
        wednesdayFrom.disableProperty().bind(wednesdayCheckBox.selectedProperty().not());
        wednesdayTo.disableProperty().bind(wednesdayCheckBox.selectedProperty().not());

        LocalTimeTextField thursdayFrom = new LocalTimeTextField();
        LocalTimeTextField thursdayTo = new LocalTimeTextField();
        HBox thursdayTimeRange = new HBox(thursdayFrom, new Label("to:"), thursdayTo);
        thursdayTimeRange.setSpacing(10);
        thursdayTimeRange.setAlignment(Pos.CENTER);

        CheckBox thursdayCheckBox = new CheckBox("Thursday");
        thursdayCheckBox.setSelected(course.getThursdayStart() != null && course.getThursdayEnd() != null);
        thursdayFrom.disableProperty().bind(thursdayCheckBox.selectedProperty().not());
        thursdayTo.disableProperty().bind(thursdayCheckBox.selectedProperty().not());

        LocalTimeTextField fridayFrom = new LocalTimeTextField();
        LocalTimeTextField fridayTo = new LocalTimeTextField();
        HBox fridayTimeRange = new HBox(fridayFrom, new Label("to:"), fridayTo);
        fridayTimeRange.setSpacing(10);
        fridayTimeRange.setAlignment(Pos.CENTER);

        CheckBox fridayCheckBox = new CheckBox("Friday");
        fridayCheckBox.setSelected(course.getFridayStart() != null && course.getFridayEnd() != null);
        fridayFrom.disableProperty().bind(fridayCheckBox.selectedProperty().not());
        fridayTo.disableProperty().bind(fridayCheckBox.selectedProperty().not());

        LocalTimeTextField saturdayFrom = new LocalTimeTextField();
        LocalTimeTextField saturdayTo = new LocalTimeTextField();
        HBox saturdayTimeRange = new HBox(saturdayFrom, new Label("to:"), saturdayTo);
        saturdayTimeRange.setSpacing(10);
        saturdayTimeRange.setAlignment(Pos.CENTER);

        CheckBox saturdayCheckBox = new CheckBox("Saturday");
        saturdayCheckBox.setSelected(course.getSaturdayStart() != null && course.getSaturdayEnd() != null);
        saturdayFrom.disableProperty().bind(saturdayCheckBox.selectedProperty().not());
        saturdayTo.disableProperty().bind(saturdayCheckBox.selectedProperty().not());

        if (course.getMondayStart() != null) {
            mondayFrom.setLocalTime(course.getMondayStart());
        }
        if (course.getMondayEnd() != null) {
            mondayTo.setLocalTime(course.getMondayEnd());
        }
        if (course.getTuesdayStart() != null) {
            tuesdayFrom.setLocalTime(course.getTuesdayStart());
        }
        if (course.getTuesdayEnd() != null) {
            tuesdayTo.setLocalTime(course.getTuesdayEnd());
        }
        if (course.getWednesdayStart() != null) {
            wednesdayFrom.setLocalTime(course.getWednesdayStart());
        }
        if (course.getWednesdayEnd() != null) {
            wednesdayTo.setLocalTime(course.getWednesdayEnd());
        }
        if (course.getThursdayStart() != null) {
            thursdayFrom.setLocalTime(course.getThursdayStart());
        }
        if (course.getThursdayEnd() != null) {
            thursdayTo.setLocalTime(course.getThursdayEnd());
        }
        if (course.getFridayStart() != null) {
            fridayFrom.setLocalTime(course.getFridayStart());
        }
        if (course.getFridayEnd() != null) {
            fridayTo.setLocalTime(course.getFridayEnd());
        }
        if (course.getSaturdayStart() != null) {
            saturdayFrom.setLocalTime(course.getSaturdayStart());
        }
        if (course.getSaturdayEnd() != null) {
            saturdayTo.setLocalTime(course.getSaturdayEnd());
        }

        grid.add(new Label("Course Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Course Number:"), 0, 1);
        grid.add(numField, 1, 1);
        grid.add(new Label("Course Section:"), 0, 2);
        grid.add(sectField, 1, 2);
        grid.add(new Label("CRN:"), 0, 3);
        grid.add(crnField, 1, 3);
        grid.add(mondayCheckBox, 0, 4);
        grid.add(mondayTimeRange, 1, 4);
        grid.add(tuesdayCheckBox, 0, 5);
        grid.add(tuesdayTimeRange, 1, 5);
        grid.add(wednesdayCheckBox, 0, 6);
        grid.add(wednesdayTimeRange, 1, 6);
        grid.add(thursdayCheckBox, 0, 7);
        grid.add(thursdayTimeRange, 1, 7);
        grid.add(fridayCheckBox, 0, 8);
        grid.add(fridayTimeRange, 1, 8);
        grid.add(saturdayCheckBox, 0, 9);
        grid.add(saturdayTimeRange, 1, 9);

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
        Platform.runLater(nameField::requestFocus);

        // Convert the result to a student when the register button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                course.setCrn(Integer.parseInt(crnField.getText()));
                course.setName(nameField.getText());
                course.setNumber(numField.getText());
                course.setSection(Integer.parseInt(sectField.getText()));
                if (mondayCheckBox.isSelected()) {
                    course.setMondayStart(mondayFrom.getLocalTime());
                    course.setMondayEnd(mondayTo.getLocalTime());
                } else {
                    course.setMondayStart(null);
                    course.setMondayEnd(null);
                }
                if (tuesdayCheckBox.isSelected()) {
                    course.setTuesdayStart(tuesdayFrom.getLocalTime());
                    course.setTuesdayEnd(tuesdayTo.getLocalTime());
                } else {
                    course.setTuesdayStart(null);
                    course.setTuesdayEnd(null);
                }
                if (wednesdayCheckBox.isSelected()) {
                    course.setWednesdayStart(wednesdayFrom.getLocalTime());
                    course.setWednesdayEnd(wednesdayTo.getLocalTime());
                } else {
                    course.setWednesdayStart(null);
                    course.setWednesdayEnd(null);
                }
                if (thursdayCheckBox.isSelected()) {
                    course.setThursdayStart(thursdayFrom.getLocalTime());
                    course.setThursdayEnd(thursdayTo.getLocalTime());
                } else {
                    course.setThursdayStart(null);
                    course.setThursdayEnd(null);
                }
                if (fridayCheckBox.isSelected()) {
                    course.setFridayStart(fridayFrom.getLocalTime());
                    course.setFridayEnd(fridayTo.getLocalTime());
                } else {
                    course.setFridayStart(null);
                    course.setFridayEnd(null);
                }
                if (saturdayCheckBox.isSelected()) {
                    course.setSaturdayStart(saturdayFrom.getLocalTime());
                    course.setSaturdayEnd(saturdayTo.getLocalTime());
                } else {
                    course.setSaturdayStart(null);
                    course.setSaturdayEnd(null);
                }
                return course;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Show a throwable dialog.
     *
     * @param e The {@link Throwable} to display
     */
    public static void showThrowableDialog(Throwable e) {
        // Log the throwable.
        log.error(e.getMessage(), e);

        // Construct the dialog.
        Alert alert = newAlert(AlertType.ERROR);
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

        // Set expandable Throwable into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Create a new alert.
     *
     * @param alertType The {@link AlertType}
     * @return The alert
     */
    private static Alert newAlert(AlertType alertType) {
        Alert alert = new Alert(alertType);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.ICON_URL));
        alert.getDialogPane().getStylesheets().add(Constants.STYLESHEET_URL);
        return alert;
    }

    /**
     * Create a new dialog.
     *
     * @param <T> The return type of the dialog.
     * @return The dialog
     */
    private static <T> Dialog<T> newDialog() {
        Dialog<T> dialog = new Dialog<>();
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.ICON_URL));
        dialog.getDialogPane().getStylesheets().add(Constants.STYLESHEET_URL);
        return dialog;
    }

}
