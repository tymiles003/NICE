package com.sudicode.nice.dao;

import com.google.inject.Inject;
import com.sudicode.nice.model.Student;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO for students.
 */
public class Students {

    private final DataSource dataSource;

    /**
     * Construct a new {@link Students}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    @Inject
    public Students(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return A new {@link Student} with the {@link DataSource} initialized.
     */
    public Student newInstance() {
        Student student = new Student();
        student.setDataSource(dataSource);
        return student;
    }

    /**
     * Obtain a list of {@link Student Students}.
     *
     * @return A list of all students
     * @throws SQLException if a database access error occurs
     */
    public List<Student> list() throws SQLException {
        List<Student> list = new QueryRunner(dataSource).query(
                "SELECT * FROM Students",
                new BeanListHandler<>(Student.class));
        list.forEach(student -> student.setDataSource(dataSource));
        return list;
    }

    /**
     * Obtain a single {@link Student} by ID.
     *
     * @param studentId The student ID
     * @return An {@link Optional} which contains the student iff one exists with the given ID
     * @throws SQLException if a database access error occurs
     */
    public Optional<Student> getById(String studentId) throws SQLException {
        Student student = new QueryRunner(dataSource).query(
                "SELECT * FROM Students WHERE studentId = ?",
                new BeanHandler<>(Student.class),
                studentId);
        if (student == null) {
            return Optional.empty();
        }
        student.setDataSource(dataSource);
        return Optional.of(student);
    }

}
