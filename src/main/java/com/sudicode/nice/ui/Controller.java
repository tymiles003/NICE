package com.sudicode.nice.ui;

import com.diffplug.common.base.Errors;
import com.sudicode.nice.Constants;
import com.sudicode.nice.Util;
import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.Student;
import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.hardware.CardTerminalDevice;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FOREVER;
import static org.hamcrest.Matchers.*;

/**
 * FXML controller class.
 */
public class Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private CardTerminal cardTerminal;
    private CardReader cardReader;

    @FXML
    private BorderPane window;
    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn<Student, String> idCol;
    @FXML
    private TableColumn<Student, String> lastNameCol;
    @FXML
    private TableColumn<Student, String> firstNameCol;
    @FXML
    private TableColumn<Student, String> middleNameCol;
    @FXML
    private TableColumn<Student, String> statusCol;
    @FXML
    private ComboBox<Course> courseSelect;

    private Text placeholder;

    /**
     * Initalize dependencies here, since the {@link Controller} cannot be instantiated by Guice.
     */
    public Controller() {
        try {
            cardTerminal = getCardTerminal();
            cardReader = new CardReader(new CardTerminalDevice(cardTerminal));
        } catch (CardException | RuntimeException e) {
            DialogFactory.showThrowableDialog(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Open database connection
        Util.openDbConnection();

        // Initialize choice box.
        courseSelect.setItems(FXCollections.observableArrayList(Course.findAll()));

        // Initialize students table.
        placeholder = new Text("Create or select a course to begin.");
        placeholder.setFont(Font.font(Constants.PLACEHOLDER_SIZE));
        placeholder.wrappingWidthProperty().bind(window.widthProperty());
        placeholder.setTextAlignment(TextAlignment.CENTER);
        studentsTable.setPlaceholder(placeholder);
        courseSelect.getSelectionModel().selectedItemProperty().addListener((x, y, z) -> loadStudents());

        // Listen for cards.
        Util.submitBackgroundTask(Errors.dialog().wrap(() -> {
            while (!Thread.interrupted()) {
                // Wait until a valid course is selected.
                await().atMost(FOREVER).until(this::getSelectedCourse, is(notNullValue()));

                // Get UID.
                int uid = cardReader.readUID();
                Optional<Student> oStudent = Optional.ofNullable(Student.findById(uid));

                // Get selected course.
                Course course = getSelectedCourse();
                if (course == null) continue;

                // Either mark student's attendance, enroll the student, or register the student.
                if (oStudent.isPresent() && studentsTable.getItems().contains(oStudent.get())) {
                    oStudent.get().attend(course);
                    Platform.runLater(() -> studentsTable.refresh());
                } else if (oStudent.isPresent()) {
                    Platform.runLater(() -> enrollStudent(oStudent.get(), course));
                } else {
                    Platform.runLater(() -> addStudent(uid, course));
                }
                cardTerminal.waitForCardAbsent(0);
            }
        }));
    }

    /**
     * Ask the instructor if they wish to add a new student. If they accept, provide the dialog which allows them to
     * do so.
     *
     * @param studentId The new student's ID
     */
    private void addStudent(int studentId) {
        addStudent(studentId, null);
    }

    /**
     * Ask the instructor if they wish to add a new student. If they accept, provide the dialog which allows them to
     * do so.
     *
     * @param studentId The new student's ID
     * @param course    Course to immediately enroll in
     */
    private void addStudent(int studentId, Course course) {
        // Ask the instructor if they wish to add a new student.
        if (DialogFactory.getAddStudentDialog().showAndWait().orElse(null) == ButtonType.OK) {
            Student newStudent = new Student();
            newStudent.setStudentId(studentId);
            DialogFactory.showStudentDialog(newStudent).ifPresent(Errors.dialog().wrap(s -> {
                s.checkedInsert();
                if (course != null) {
                    s.enroll(course);
                    studentsTable.getItems().add(s);
                }
            }));
        }
    }

    /**
     * Ask the instructor if they wish to enroll the student. If they accept, proceed with enrollment.
     *
     * @param student The {@link Student} to enroll
     * @param course  The {@link Course} to enroll in
     */
    private void enrollStudent(Student student, Course course) {
        if (DialogFactory.getEnrollStudentDialog(student, course).showAndWait().orElse(null) == ButtonType.OK) {
            try {
                student.enroll(course);
                studentsTable.getItems().add(student);
            } catch (SQLException e) {
                DialogFactory.showThrowableDialog(e);
            }
        }
    }

    /**
     * Loads students into the students table.
     */
    private void loadStudents() {
        // Get selected course.
        Course selected = getSelectedCourse();

        // Initialize table.
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setOnEditCommit(edit -> {
            Student student = edit.getRowValue();
            student.setLastName(edit.getNewValue());
            try {
                student.checkedUpdate();
            } catch (SQLException e) {
                DialogFactory.showThrowableDialog(e);
            }
        });

        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setOnEditCommit(edit -> {
            Student student = edit.getRowValue();
            student.setFirstName(edit.getNewValue());
            try {
                student.checkedUpdate();
            } catch (SQLException e) {
                DialogFactory.showThrowableDialog(e);
            }
        });

        middleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameCol.setOnEditCommit(edit -> {
            Student student = edit.getRowValue();
            student.setMiddleName(edit.getNewValue());
            try {
                student.checkedUpdate();
            } catch (SQLException e) {
                DialogFactory.showThrowableDialog(e);
            }
        });

        statusCol.setCellValueFactory(param -> {
            try {
                return new SimpleStringProperty(param.getValue().getStatus(selected));
            } catch (SQLException e) {
                DialogFactory.showThrowableDialog(e);
                return new SimpleStringProperty();
            }
        });

        // Populate table.
        if (selected == null) {
            placeholder.setText("Create or select a course to begin.");
            studentsTable.getItems().clear();
        } else {
            placeholder.setText(String.format("Tap student ID card to enroll them in %s.", selected));
            int crn = getSelectedCourse().getCrn();
            studentsTable.setItems(FXCollections.observableList(Student.findByCrn(crn)));
        }
    }

    /**
     * Update a student.
     */
    public void updateStudent() {
        courseSelect.getSelectionModel().clearSelection();
        DialogFactory.showAsyncWaitForCardDialog(cardReader, Errors.dialog().wrap(uid -> {
            Optional<Student> oStudent = Optional.ofNullable(Student.findById(uid));
            if (oStudent.isPresent()) {
                Optional<Student> result = DialogFactory.showStudentDialog(oStudent.get());
                if (result.isPresent()) {
                    Student student = result.get();
                    student.checkedUpdate();
                    DialogFactory.showObjectUpdatedDialog(student);
                }
            } else {
                addStudent(uid);
            }
        }));
    }

    /**
     * Delete a student.
     */
    public void deleteStudent() {
        courseSelect.getSelectionModel().clearSelection();
        DialogFactory.showAsyncWaitForCardDialog(cardReader, Errors.dialog().wrap(uid -> {
            Optional<Student> oStudent = Optional.ofNullable(Student.findById(uid));
            if (oStudent.isPresent()) {
                DialogFactory.getDeleteStudentDialog(oStudent.get()).showAndWait().ifPresent(Errors.dialog().wrap(buttonType -> {
                    Student student = oStudent.get();
                    if (buttonType == ButtonType.OK) {
                        student.checkedDelete();
                    }
                    DialogFactory.showObjectDeletedDialog(student);
                }));
            } else {
                addStudent(uid);
            }
        }));
    }

    /**
     * Provide the dialog for the instructor to add a course.
     */
    public void addCourse() {
        DialogFactory.showCourseDialog(new Course()).ifPresent(Errors.dialog().wrap(c -> {
            c.checkedInsert();
            courseSelect.getItems().add(c);
            courseSelect.getSelectionModel().selectLast();
        }));
    }

    /**
     * Update the selected course.
     */
    public void updateCourse() {
        int index = getSelectedCourseIndex();
        Course selected = getSelectedCourse();
        if (selected == null) {
            DialogFactory.showNoCourseSelectedDialog();
        } else {
            DialogFactory.showCourseDialog(selected).ifPresent(Errors.dialog().wrap(c -> {
                c.checkedUpdate();
                courseSelect.getItems().set(index, c);
                DialogFactory.showObjectUpdatedDialog(c);
            }));
        }
    }

    /**
     * Delete the selected course.
     */
    public void deleteCourse() {
        int index = getSelectedCourseIndex();
        Course selected = getSelectedCourse();
        if (selected == null) {
            DialogFactory.showNoCourseSelectedDialog();
        } else {
            DialogFactory.getDeleteCourseDialog(selected).showAndWait().ifPresent(Errors.dialog().wrap(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    selected.checkedDelete();
                    courseSelect.getItems().remove(index);
                    loadStudents();
                    DialogFactory.showObjectDeletedDialog(selected);
                }
            }));
        }
    }

    /**
     * @return Selected student
     */
    private Student getSelectedStudent() {
        return studentsTable.getSelectionModel().getSelectedItem();
    }

    /**
     * @return Selected student index
     */
    private int getSelectedStudentIndex() {
        return studentsTable.getSelectionModel().getSelectedIndex();
    }

    /**
     * @return Selected course
     */
    private Course getSelectedCourse() {
        return courseSelect.getSelectionModel().getSelectedItem();
    }

    /**
     * @return Selected course index
     */
    private int getSelectedCourseIndex() {
        return courseSelect.getSelectionModel().getSelectedIndex();
    }

    /**
     * Terminate the application.
     */
    public void quit() {
        Util.closeDbConnection();
        Platform.exit();
    }

    /**
     * Get card reader.
     *
     * @return The {@link CardTerminal}
     * @throws CardException if the card operation failed
     */
    private CardTerminal getCardTerminal() throws CardException {
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = terminalFactory.terminals().list();
        log.info("Available terminals: {}", terminals);
        CardTerminal cardTerminal = terminals.get(0);
        log.info("Commands will be sent to {}", cardTerminal.getName());
        return cardTerminal;
    }

    // TODO: Implement.
    public void exportAttendanceReport() {
        DialogFactory.showThrowableDialog(new UnsupportedOperationException("Not yet implemented!"));
    }

    // TODO: Implement.
    public void registerStudent() {
        DialogFactory.showThrowableDialog(new UnsupportedOperationException("Not yet implemented!"));
    }

    // TODO: Implement.
    public void dropStudent() {
        DialogFactory.showThrowableDialog(new UnsupportedOperationException("Not yet implemented!"));
    }

}
