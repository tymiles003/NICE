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

/**
 * FXML controller class.
 */
public class Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final CardTerminal cardTerminal;
    private final CardReader cardReader;
    private final DialogFactory dialogFactory;
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
        dialogFactory = injector.getInstance(DialogFactory.class);
        studentDAO = injector.getInstance(StudentDAO.class);
        courseDAO = injector.getInstance(CourseDAO.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize choice box.
            courseSelect.getItems().addAll(courseDAO.list());

            // Initialize students table.
            loadStudents();

            // Listen for cards.
            submitBackgroundTask(() -> {
                while (true) {
                    try {
                        String uid = cardReader.readUID();
                        Optional<Student> student = studentDAO.getById(uid);
                        if (student.isPresent()) {
                            // TODO: Mark attendance
                            log.info("{} is present.", student.get());
                        } else {
                            Platform.runLater(() -> addStudent(uid));
                        }
                        cardTerminal.waitForCardAbsent(0);
                    } catch (CardException | SQLException e) {
                        log.error("Caught exception while listening for cards.", e);
                        DialogFactory.showExceptionDialog(e);
                    }
                }
            });
        } catch (SQLException e) {
            log.error("Caught exception during initialization.", e);
            DialogFactory.showExceptionDialog(e);
        }
    }

    /**
     * Ask the instructor if they wish to add a new student. If they accept, provide the dialog which allows them to
     * do so.
     *
     * @param studentId The new student's ID
     */
    private void addStudent(String studentId) {
        // Ask the instructor if they wish to add a new student.
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Student Not Found");
        confirm.setHeaderText("Student was not found in database.");
        confirm.setContentText("Register the student?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            dialogFactory.showNewStudentDialog(studentId).ifPresent(s -> {
                try {
                    s.save();
                    refreshStudents();
                } catch (SQLException e) {
                    log.error("Could not register student.", e);
                    DialogFactory.showExceptionDialog(e);
                }
            });
        }
    }

    /**
     * Delete the selected student.
     */
    public void deleteStudent() {
        // TODO: Confirmation dialog

        try {
            Student s = studentsTable.getSelectionModel().getSelectedItem();
            if (s != null) {
                s.delete();
                refreshStudents();
            }
        } catch (SQLException e) {
            log.error("Could not delete student.", e);
            DialogFactory.showExceptionDialog(e);
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
                student.save();
            } catch (SQLException e) {
                log.error("Caught exception while updating student.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });

        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setOnEditCommit(edit -> {
            Student student = edit.getRowValue();
            student.setFirstName(edit.getNewValue());
            try {
                student.save();
            } catch (SQLException e) {
                log.error("Caught exception while updating student.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });

        middleNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameCol.setOnEditCommit(edit -> {
            Student student = edit.getRowValue();
            student.setMiddleName(edit.getNewValue());
            try {
                student.save();
            } catch (SQLException e) {
                log.error("Caught exception while updating student.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate table.
        studentsTable.setItems(FXCollections.observableList(studentDAO.list()));
    }

    /**
     * Refresh the students table.
     */
    private void refreshStudents() {
        try {
            studentsTable.getItems().setAll(studentDAO.list());
            studentsTable.refresh();
        } catch (SQLException e) {
            log.error("Could not refresh students table.", e);
            DialogFactory.showExceptionDialog(e);
        }
    }

    public void addCourse() {
        dialogFactory.showNewCourseDialog().ifPresent(c -> {
            try {
                c.save();
                courseSelect.getItems().add(c);
            } catch (SQLException e) {
                log.error("Could not add course.", e);
                DialogFactory.showExceptionDialog(e);
            }
        });
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
