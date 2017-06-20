package com.sudicode.nice.model;

import com.sudicode.nice.dao.Students;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Model class for students.
 */
public class Student {

    private DataSource dataSource;

    private String studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    /**
     * Default constructor. Avoid calling this directly, as it does not initialize the {@link DataSource}. Use
     * {@link Students#newInstance()} instead.
     * <p>
     * Left public to allow access from {@link org.apache.commons.dbutils.DbUtils DbUtils}.
     */
    public Student() {
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
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
     * Save ("upsert") this student into its dataSource.
     *
     * @throws SQLException if a database access error occurs
     */
    public void save() throws SQLException {
        QueryRunner run = new QueryRunner(getDataSource());
        String sql = "INSERT INTO Students VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE firstname = ?, middlename = ?, lastname = ?, email = ?";
        run.update(sql, getStudentId(), getFirstName(), getMiddleName(), getLastName(), getEmail(),
                getFirstName(), getMiddleName(), getLastName(), getEmail());
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

}