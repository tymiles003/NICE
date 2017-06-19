package com.sudicode.nice.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sudicode.nice.di.NiceModule;
import com.sudicode.nice.hardware.CardReader;
import com.sudicode.nice.model.Course;
import com.sudicode.nice.model.Student;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.smartcardio.CardTerminal;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML controller class.
 */
public class Controller implements Initializable {

    private final DataSource dataSource;
    private final CardTerminal cardTerminal;
    private final CardReader cardReader;

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
        this.dataSource = injector.getInstance(DataSource.class);
        this.cardTerminal = injector.getInstance(CardTerminal.class);
        this.cardReader = injector.getInstance(CardReader.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize choice box.
            courseSelect.getItems().addAll(Course.list(dataSource));

            // TODO: Add this under a listener for courseSelect
            // Initialize table.
            idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Populate table.
            List<Student> students = Student.list(dataSource);
            studentsTable.setItems(FXCollections.observableList(students));

            // Listen for cards.
            submitBackgroundTask(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        String uid = cardReader.readUID();
                        // TODO: Handle reading of UID.
                        cardTerminal.waitForCardAbsent(0);
                        // TODO: Handle absence of card.
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run a task in the background. The task will be run in a <strong>daemon</strong> thread. Not recommended for
     * tasks which require resource cleanup.
     *
     * @param task The {@link Task} to run
     */
    private void submitBackgroundTask(Task<?> task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}
