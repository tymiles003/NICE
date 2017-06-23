package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
        if (getKey() == null) {
            setKey(studentId);
        }
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
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

    /**
     * Get the status (present, late, absent) of this {@link Student} with regards to a certain {@link Course}.
     *
     * @param course The {@link Course}
     * @return "present", "late", or "absent"
     * @throws SQLException if a database access error occurs
     */
    public String getStatus(Course course) throws SQLException {
        // Query the database.
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "SELECT datetime "
                + "FROM Attendances "
                + "WHERE studentid = ? AND crn = ?";
        Timestamp ts = run.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }
            return rs.getTimestamp("datetime");
        }, getKey(), course.getKey());

        // Return status.
        if (ts == null) {
            return ("absent");
        } else {
            LocalDateTime ldt = ts.toLocalDateTime();
            // TODO: Logic here ...
            return "present";
        }
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
        setKey(getStudentId());
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
        if (run.update(sql, getStudentId(), getFirstName(), getMiddleName(), getLastName(), getEmail(), getKey()) != 1) {
            throw new SQLException("Update failed.");
        }
        setKey(getStudentId());
    }

    /**
     * Delete this student from its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void delete() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        if (run.update("DELETE FROM Students WHERE studentid = ?", getKey()) != 1) {
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
        if (run.update("INSERT INTO Registrations (studentid, crn) VALUES (?, ?)", getKey(), course.getKey()) != 1) {
            throw new SQLException("Enroll failed.");
        }
    }

    /**
     * Mark this student as present in a {@link Course}.
     *
     * @param course The {@link Course} to attend
     * @throws SQLException if a database access error occurs
     */
    public void attend(Course course) throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        if (run.update("INSERT INTO Attendances (studentid, crn) VALUES (?, ?)", getKey(), course.getKey()) != 1) {
            throw new SQLException("Attend failed.");
        }
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

}