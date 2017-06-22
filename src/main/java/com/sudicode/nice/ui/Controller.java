package com.sudicode.nice.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sudicode.nice.database.Course;
import com.sudicode.nice.database.CourseDAO;
import com.sudicode.nice.database.Student;
import com.sudicode.nice.database.StudentDAO;
import com.sudicode.nice.di.NiceModule;
import com.sudicode.nice.hardware.CardReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
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

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final CardTerminal cardTerminal;
    private final CardReader cardReader;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

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
            courseSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    loadStudents();
                } catch (SQLException e) {
                    log.error("Could not load students.", e);
                    DialogFactory.showExceptionDialog(e);
                }
            });

            // Listen for cards.
            submitBackgroundTask(() -> {
                while (true) {
                    try {
                        // Get selected course.
                        Course course = await()
                                .atMost(FOREVER)
                                .until(() -> courseSelect.getSelectionModel().getSelectedItem(), is(notNullValue()));

                        // Get UID.
                        int uid = cardReader.readUID();
                        Optional<Student> maybeStudent = studentDAO.getById(uid);

                        // Either mark student's attendance, enroll the student, or register the student.
                        if (maybeStudent.isPresent() && studentsTable.getItems().contains(maybeStudent.get())) {
                            // TODO: Mark attendance
                            log.info("{} is present.", maybeStudent.get());
                        } else if (maybeStudent.isPresent()) {
                            Platform.runLater(() -> enrollStudent(maybeStudent.get(), course));
                        } else {
                            Platform.runLater(() -> addStudent(uid, course));
                        }
                        cardTerminal.waitForCardAbsent(0);
                    } catch (CardException | SQLException e) {
                        log.error("Could not listen for cards.", e);
                        Platform.runLater(() -> DialogFactory.showExceptionDialog(e));
                        return;
                    }
                }
            });
        } catch (SQLException e) {
            log.error("Could not initialize.", e);
            DialogFactory.showExceptionDialog(e);
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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Student Not Found");
        confirm.setHeaderText("Student was not found in database.");
        confirm.setContentText("Register the student?");
        if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
            Student newStudent = studentDAO.newStudent();
            newStudent.setStudentId(studentId);
            DialogFactory.showStudentDialog(newStudent).ifPresent(s -> {
                try {
                    s.insert();
                    s.enroll(course);
                    studentsTable.getItems().add(s);
                } catch (SQLException e) {
                    log.error("Could not register student.", e);
                    DialogFactory.showExceptionDialog(e);
                }
            });
        }
    }

    /**
     * Ask the instructor if they wish to enroll the student. If they accept, proceed with enrollment.
     *
     * @param student The {@link Student} to enroll
     * @param course  The {@link Course} to enroll in
     */
    private void enrollStudent(Student student, Course course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Student Not Enrolled");
        confirm.setHeaderText(String.format("%s is not enrolled in %s.", student, course));
        confirm.setContentText("Enroll the student?");
        if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                student.enroll(course);
                studentsTable.getItems().add(student);
            } catch (SQLException e) {
                log.error("Could not enroll {} in {}.", student, course);
            }
        }
    }

    /**
     * Loads students into the students table.
     *
     * @throws SQLException if a database access error occurs
     */
    private void loadStudents() throws SQLException {
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
                log.error("Could not update student.", e);
                DialogFactory.showExceptionDialog(e);
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
                log.error("Could not update student.", e);
                DialogFactory.showExceptionDialog(e);
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
                log.error("Could not update student.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate table.
        Course selected = courseSelect.getSelectionModel().getSelectedItem();
        if (selected == null) {
            // TODO: A more elegant solution is needed here.
            studentsTable.getItems().clear();
        } else {
            int crn = courseSelect.getSelectionModel().getSelectedItem().getCrn();
            studentsTable.setItems(FXCollections.observableList(studentDAO.list(crn)));
        }
    }

    /**
     * Update the selected student.
     */
    public void updateStudent() {
        int index = studentsTable.getSelectionModel().getSelectedIndex();
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        DialogFactory.showStudentDialog(selected).ifPresent(s -> {
            try {
                s.update();
                studentsTable.getItems().set(index, s);
            } catch (SQLException e) {
                log.error("Could not update student.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });
    }

    /**
     * Delete the selected student.
     */
    public void deleteStudent() {
        int index = studentsTable.getSelectionModel().getSelectedIndex();
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            selected.delete();
            studentsTable.getItems().remove(index);
        } catch (SQLException e) {
            log.error("Could not delete student.", e);
            DialogFactory.showExceptionDialog(e);
        }
    }

    /**
     * Provide the dialog for the instructor to add a course.
     */
    public void addCourse() {
        DialogFactory.showCourseDialog(courseDAO.newCourse()).ifPresent(c -> {
            try {
                c.insert();
                courseSelect.getItems().add(c);
                courseSelect.getSelectionModel().selectLast();
            } catch (SQLException e) {
                log.error("Could not add course.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });
    }

    /**
     * Update the selected course.
     */
    public void updateCourse() {
        int index = courseSelect.getSelectionModel().getSelectedIndex();
        Course selected = courseSelect.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        DialogFactory.showCourseDialog(selected).ifPresent(c -> {
            try {
                c.update();
                courseSelect.getItems().set(index, c);
            } catch (SQLException e) {
                log.error("Could not update course.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });
    }

    /**
     * Delete the selected course.
     */
    public void deleteCourse() {
        int index = courseSelect.getSelectionModel().getSelectedIndex();
        Course selected = courseSelect.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            selected.delete();
            courseSelect.getItems().remove(index);
            loadStudents();
        } catch (SQLException e) {
            log.error("Could not delete course.", e);
            DialogFactory.showExceptionDialog(e);
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

}
