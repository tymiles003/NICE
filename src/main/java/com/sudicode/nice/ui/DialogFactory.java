package com.sudicode.nice.ui;

import com.google.inject.Inject;
import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.CourseDAO;
import com.sudicode.nice.database.Student;
import com.sudicode.nice.database.StudentDAO;
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

import javax.smartcardio.CardException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Generates JavaFX dialog boxes.
 */
public class DialogFactory {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    /**
     * Construct a new {@link DialogFactory}.
     *
     * @param studentDAO The {@link StudentDAO} instance.
     * @param courseDAO  The {@link CourseDAO} instance.
     */
    @Inject
    public DialogFactory(StudentDAO studentDAO, CourseDAO courseDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }

    /**
     * Shows a {@link Dialog} which allows the instructor to add a new student.
     *
     * @param studentId The new student's ID
     * @return The result of {@link Dialog#showAndWait()}
     */
    public Optional<Student> showNewStudentDialog(int studentId) {
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
                Student s = studentDAO.newStudent();
                s.setStudentId(studentId);
                s.setFirstName(firstName.getText());
                s.setMiddleName(middleName.getText());
                s.setLastName(lastName.getText());
                s.setEmail(email.getText());
                return s;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public Optional<Course> showNewCourseDialog() {
        // Create the custom dialog.
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("New Course");
        dialog.setHeaderText("Add a new course.");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add Course", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField crn = new TextField();
        crn.setPromptText("CRN");
        TextField name = new TextField();
        name.setPromptText("Course Name");
        TextField number = new TextField();
        number.setPromptText("Course Number");
        TextField section = new TextField();
        section.setPromptText("Course Section");

        grid.add(new Label("CRN:"), 0, 0);
        grid.add(crn, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Course Number:"), 0, 2);
        grid.add(number, 1, 2);
        grid.add(new Label("Course Section:"), 0, 3);
        grid.add(section, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first name field by default.
        Platform.runLater(crn::requestFocus);

        // Convert the result to a student when the register button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                Course c = courseDAO.newCourse();
                c.setCrn(Integer.parseInt(crn.getText()));
                c.setName(name.getText());
                c.setNumber(number.getText());
                c.setSection(Integer.parseInt(section.getText()));
                return c;
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
