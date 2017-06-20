package com.sudicode.nice.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sudicode.nice.dao.Courses;
import com.sudicode.nice.dao.Students;
import com.sudicode.nice.di.NiceModule;
import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.model.Course;
import com.sudicode.nice.model.Student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private final Dialogs dialogs;
    private final Students students;
    private final Courses courses;

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
        dialogs = injector.getInstance(Dialogs.class);
        students = injector.getInstance(Students.class);
        courses = injector.getInstance(Courses.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize choice box.
            courseSelect.getItems().addAll(courses.list());

            // TODO: Add this under a listener for courseSelect
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
                    Dialogs.showExceptionDialog(e);
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
                    Dialogs.showExceptionDialog(e);
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
                    Dialogs.showExceptionDialog(e);
                }
            });

            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Populate table.
            studentsTable.setItems(FXCollections.observableList(students.list()));

            // Listen for cards.
            submitBackgroundTask(() -> {
                while (true) {
                    try {
                        String uid = cardReader.readUID();
                        Optional<Student> student = students.getById(uid);
                        if (student.isPresent()) {
                            // TODO: Mark attendance
                            System.out.println("Welcome " + student.get());
                        } else {
                            Platform.runLater(() -> {
                                dialogs.showNewStudentDialog(uid);
                                refreshStudents();
                            });
                        }
                        cardTerminal.waitForCardAbsent(0);
                    } catch (CardException | SQLException e) {
                        log.error("Caught exception while listening for cards.", e);
                        Dialogs.showExceptionDialog(e);
                    }
                }
            });
        } catch (SQLException e) {
            log.error("Caught exception during initialization.", e);
            Dialogs.showExceptionDialog(e);
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
     * Refresh the students table.
     */
    private void refreshStudents() {
        try {
            studentsTable.getItems().setAll(students.list());
        } catch (SQLException e) {
            log.error("Caught exception while refreshing students table.", e);
            Dialogs.showExceptionDialog(e);
        }
        studentsTable.refresh();
    }

}
