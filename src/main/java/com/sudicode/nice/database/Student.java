package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Student in the database.
 */
@EqualsAndHashCode
public class Student {

    private final transient DataSource dataSource;

    /**
     * Database key, which may differ in state from studentId
     */
    private transient Integer key;

    private int studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    /**
     * Constructor.
     *
     * @param dataSource The {@link DataSource} that this student originates from
     */
    Student(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
        if (key == null) {
            key = studentId;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // TODO
    public String getStatus() {
        return "absent";
    }

    /**
     * Insert this student into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void insert() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "INSERT INTO Students VALUES (?, ?, ?, ?, ?)";
        if (run.update(sql, getStudentId(), getFirstName(), getMiddleName(), getLastName(), getEmail()) != 1) {
            throw new SQLException("Insert failed.");
        }
    }

    /**
     * Update this student in its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void update() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "UPDATE Students SET studentid = ?, firstname = ?, middlename = ?, lastname = ?, email = ? "
                + "WHERE studentid = ?";
        if (run.update(sql, getStudentId(), getFirstName(), getMiddleName(), getLastName(), getEmail(), key) != 1) {
            throw new SQLException("Update failed.");
        }
        key = getStudentId();
    }

    /**
     * Delete this student from its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void delete() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        if (run.update("DELETE FROM Students WHERE studentid = ?", key) != 1) {
            throw new SQLException("Delete failed.");
        }
    }

    /**
     * Enroll this student in a {@link Course}.
     *
     * @param course The {@link Course} to enroll in
     * @throws SQLException if a database access error occurs
     */
    public void enroll(Course course) throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        if (run.update("INSERT INTO Registrations (studentid, crn) VALUES (?, ?)", key, course.getCrn()) != 1) {
            throw new SQLException("Enroll failed.");
        }
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

}