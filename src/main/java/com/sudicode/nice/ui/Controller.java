package com.sudicode.nice.ui;

import com.diffplug.common.base.Errors;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sudicode.nice.Constants;
import com.sudicode.nice.UncheckedSQLException;
import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.CourseDAO;
import com.sudicode.nice.database.Student;
import com.sudicode.nice.database.StudentDAO;
import com.sudicode.nice.di.NiceModule;
import com.sudicode.nice.hardware.CardReader;
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

import javax.smartcardio.CardTerminal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FOREVER;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * FXML controller class.
 */
public class Controller implements Initializable {

    private final CardTerminal cardTerminal;
    private final CardReader cardReader;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

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
        Injector injector = Guice.createInjector(new NiceModule());
        cardTerminal = injector.getInstance(CardTerminal.class);
        cardReader = injector.getInstance(CardReader.class);
        studentDAO = injector.getInstance(StudentDAO.class);
        courseDAO = injector.getInstance(CourseDAO.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize choice box.
            courseSelect.setItems(FXCollections.observableArrayList(courseDAO.list()));

            // Initialize students table.
            placeholder = new Text("Create or select a course to begin.");
            placeholder.setFont(Font.font(Constants.PLACEHOLDER_SIZE));
            placeholder.wrappingWidthProperty().bind(window.widthProperty());
            placeholder.setTextAlignment(TextAlignment.CENTER);
            studentsTable.setPlaceholder(placeholder);
            courseSelect.getSelectionModel().selectedItemProperty().addListener((x, y, z) -> loadStudents());

            // Listen for cards.
            submitBackgroundTask(Errors.dialog().wrap(() -> {
                while (true) {
                    // Wait until a valid course is selected.
                    await().atMost(FOREVER).until(Controller.this::getSelectedCourse, is(notNullValue()));

                    // Get UID.
                    int uid = cardReader.readUID();
                    Optional<Student> maybeStudent = studentDAO.getById(uid);

                    // Get selected course.
                    Course course = Controller.this.getSelectedCourse();
                    if (course == null) continue;

                    // Either mark student's attendance, enroll the student, or register the student.
                    if (maybeStudent.isPresent() && studentsTable.getItems().contains(maybeStudent.get())) {
                        maybeStudent.get().attend(course);
                        Platform.runLater(() -> studentsTable.refresh());
                    } else if (maybeStudent.isPresent()) {
                        Platform.runLater(() -> Controller.this.enrollStudent(maybeStudent.get(), course));
                    } else {
                        Platform.runLater(() -> Controller.this.addStudent(uid, course));
                    }
                    cardTerminal.waitForCardAbsent(0);
                }
            }));
        } catch (SQLException e) {
            DialogFactory.showThrowableDialog(e);
        }
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
            Student newStudent = studentDAO.newStudent();
            newStudent.setStudentId(studentId);
            DialogFactory.showStudentDialog(newStudent).ifPresent(Errors.dialog().wrap(s -> {
                s.insert();
                s.enroll(course);
                studentsTable.getItems().add(s);
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
        try {
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
                    student.update();
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            });

            firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            firstNameCol.setOnEditCommit(edit -> {
                Student student = edit.getRowValue();
                student.setFirstName(edit.getNewValue());
                try {
                    student.update();
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            });

            middleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
            middleNameCol.setOnEditCommit(edit -> {
                Student student = edit.getRowValue();
                student.setMiddleName(edit.getNewValue());
                try {
                    student.update();
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            });

            statusCol.setCellValueFactory(param -> {
                try {
                    return new SimpleStringProperty(param.getValue().getStatus(selected));
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }
            });

            // Populate table.
            if (selected == null) {
                placeholder.setText("Create or select a course to begin.");
                studentsTable.getItems().clear();
            } else {
                placeholder.setText(String.format("Tap student ID card to enroll them in %s.", selected));
                int crn = getSelectedCourse().getKey();
                studentsTable.setItems(FXCollections.observableList(studentDAO.list(crn)));
            }
        } catch (SQLException | UncheckedSQLException e) {
            DialogFactory.showThrowableDialog(e);
        }
    }

    /**
     * Update the selected student.
     */
    public void updateStudent() {
        int index = getSelectedStudentIndex();
        Student selected = getSelectedStudent();
        if (selected == null) return;
        DialogFactory.showStudentDialog(selected).ifPresent(Errors.dialog().wrap(s -> {
            s.update();
            studentsTable.getItems().set(index, s);
        }));
    }

    /**
     * Delete the selected student.
     */
    public void deleteStudent() {
        int index = getSelectedStudentIndex();
        Student selected = getSelectedStudent();
        if (selected == null) return;
        try {
            selected.delete();
            studentsTable.getItems().remove(index);
        } catch (SQLException e) {
            DialogFactory.showThrowableDialog(e);
        }
    }

    /**
     * Provide the dialog for the instructor to add a course.
     */
    public void addCourse() {
        DialogFactory.showCourseDialog(courseDAO.newCourse()).ifPresent(Errors.dialog().wrap(c -> {
            c.insert();
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
        if (selected == null) return;
        DialogFactory.showCourseDialog(selected).ifPresent(Errors.dialog().wrap(c -> {
            c.update();
            courseSelect.getItems().set(index, c);
        }));
    }

    /**
     * Delete the selected course.
     */
    public void deleteCourse() {
        int index = getSelectedCourseIndex();
        Course selected = getSelectedCourse();
        if (selected == null) return;
        try {
            selected.delete();
            courseSelect.getItems().remove(index);
            loadStudents();
        } catch (SQLException e) {
            DialogFactory.showThrowableDialog(e);
        }
    }

    /**
     * Run a task in the background. The task will be run in a <strong>daemon</strong> thread. Not recommended for
     * tasks which require resource cleanup.
     *
     * @param task A {@link Runnable} containing the task to run
     */
    private void submitBackgroundTask(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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

}
