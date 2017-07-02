package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Student in the database.
 */
@EqualsAndHashCode(callSuper = false)
@IdName("studentid")
public class Student extends Model {

    private int studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    public int getStudentId() {
        return isFrozen() ? studentId : getInteger("studentid");
    }

    public void setStudentId(int studentId) {
        setInteger("studentid", studentId);
    }

    public String getFirstName() {
        return isFrozen() ? firstName : getString("firstname");
    }

    public void setFirstName(String firstName) {
        setString("firstname", firstName);
    }

    public String getMiddleName() {
        return isFrozen() ? middleName : getString("middlename");
    }

    public void setMiddleName(String middleName) {
        setString("middlename", middleName);
    }

    public String getLastName() {
        return isFrozen() ? lastName : getString("lastname");
    }

    public void setLastName(String lastName) {
        setString("lastname", lastName);
    }

    public String getEmail() {
        return isFrozen() ? email : getString("email");
    }

    public void setEmail(String email) {
        setString("email", email);
    }

    /**
     * Get status (present, late, absent) in a certain {@link Course}.
     *
     * @param course The {@link Course}
     * @return "present", "late", or "absent"
     * @throws SQLException if a database access error occurs
     */
    public String getStatus(Course course) throws SQLException {
        // Query the database.
        String sql = "SELECT datetime "
                + "FROM Attendances "
                + "WHERE studentid = ? AND crn = ?";
        Timestamp timestamp = null;
        try (PreparedStatement ps = Base.connection().prepareStatement(sql)) {
            ps.setInt(1, getStudentId());
            ps.setInt(2, course.getCrn());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                timestamp = rs.getTimestamp("datetime");
            }
        }

        // Return status.
        if (timestamp == null) {
            return "absent";
        } else {
            LocalDateTime dateTime = timestamp.toLocalDateTime();
            // TODO: Need some additional logic here.
            return "present";
        }
    }

    /**
     * Insert this student into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedInsert() throws SQLException {
        if (!insert()) {
            throw new SQLException("Insert failed.");
        }
    }

    /**
     * Update this student in its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedUpdate() throws SQLException {
        if (!saveIt()) {
            throw new SQLException("Update failed.");
        }
    }

    /**
     * Delete this student from its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedDelete() throws SQLException {
        this.studentId = getStudentId();
        this.firstName = getFirstName();
        this.middleName = getMiddleName();
        this.lastName = getLastName();
        this.email = getEmail();
        if (!delete()) {
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
        String sql = "INSERT INTO Registrations (studentid, crn) VALUES (?, ?)";
        try (PreparedStatement ps = Base.connection().prepareStatement(sql)) {
            ps.setInt(1, getStudentId());
            ps.setInt(2, course.getCrn());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Enroll failed.");
            }
        }
    }

    /**
     * Mark this student as present in a {@link Course}.
     *
     * @param course The {@link Course} to attend
     * @throws SQLException if a database access error occurs
     */
    public void attend(Course course) throws SQLException {
        String sql = "INSERT INTO Attendances (studentid, crn) VALUES (?, ?)";
        try (PreparedStatement ps = Base.connection().prepareStatement(sql)) {
            ps.setInt(1, getStudentId());
            ps.setInt(2, course.getCrn());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Enroll failed.");
            }
        }
    }

    /**
     * Obtain a list of students who are enrolled in a course.
     *
     * @param crn Course registration number
     * @return List of students
     */
    public static List<Student> findByCrn(int crn) {
        String sql = "SELECT s.studentid, s.firstname, s.middlename, s.lastname, s.email "
                + "FROM Students s "
                + "NATURAL JOIN Registrations r "
                + "WHERE r.crn = ? ";
        return findBySQL(sql, crn);
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

}
