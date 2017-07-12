package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Student in the database.
 */
@EqualsAndHashCode(callSuper = false)
@IdName("studentid")
@Table("Students")
public class Student extends Model {

    private static final Logger LOG = LoggerFactory.getLogger(Student.class);

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
                + "WHERE studentid = ? AND crn = ? AND CAST(datetime AS DATE) = CURRENT_DATE() "
                + "ORDER BY datetime";
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
        LocalTime courseStart, courseEnd;
        LocalDateTime now = LocalDateTime.now();
        switch (now.getDayOfWeek()) {
            case MONDAY:
                courseStart = course.getMondayStart();
                courseEnd = course.getMondayEnd();
                break;
            case TUESDAY:
                courseStart = course.getTuesdayStart();
                courseEnd = course.getTuesdayEnd();
                break;
            case WEDNESDAY:
                courseStart = course.getWednesdayStart();
                courseEnd = course.getWednesdayEnd();
                break;
            case THURSDAY:
                courseStart = course.getThursdayStart();
                courseEnd = course.getThursdayEnd();
                break;
            case FRIDAY:
                courseStart = course.getFridayStart();
                courseEnd = course.getFridayEnd();
                break;
            case SATURDAY:
                courseStart = course.getSaturdayStart();
                courseEnd = course.getSaturdayEnd();
                break;
            case SUNDAY:
                courseStart = course.getSundayStart();
                courseEnd = course.getSundayEnd();
                break;
            default:
                throw new IllegalStateException("Invalid day of week");
        }
        if (courseStart == null || courseEnd == null) {
            return "no class";
        } else if (timestamp == null) {
            LOG.info("No attendances found on this day.");
            return "absent";
        } else {
            LocalTime attendTime = timestamp.toLocalDateTime().toLocalTime();
            LOG.info("Class runs from {} to {}. Student's earliest attendance was at {}.", courseStart, courseEnd, attendTime);
            if (attendTime.isAfter(courseEnd)) {
                return "late";
            } else {
                return "present";
            }
        }
    }

    /**
     * Delete this student from its data source.
     */
    public void deleteIt() {
        this.studentId = getStudentId();
        this.firstName = getFirstName();
        this.middleName = getMiddleName();
        this.lastName = getLastName();
        this.email = getEmail();
        delete();
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
