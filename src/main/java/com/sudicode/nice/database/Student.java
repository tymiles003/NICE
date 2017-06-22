package com.sudicode.nice.database;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Student in the database.
 */
public class Student {

    private final DataSource dataSource;

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
     * Save ("upsert") this student into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void save() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "INSERT INTO Students VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE firstname = ?, middlename = ?, lastname = ?, email = ?";
        run.update(sql, getStudentId(), getFirstName(), getMiddleName(), getLastName(), getEmail(),
                getFirstName(), getMiddleName(), getLastName(), getEmail());
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Delete this student from its data source.
     */
    public void delete() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        run.update("DELETE FROM Students WHERE studentid = ?", getStudentId());
    }

}