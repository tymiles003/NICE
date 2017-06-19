package com.sudicode.nice.model;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for students.
 */
public class Student {

    private final DataSource dataSource;

    private String studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    /**
     * Construct a new {@link Student}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    private Student(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Obtain a list of {@link Student Students}.
     *
     * @param dataSource The {@link DataSource} to access
     * @return A list of all students
     * @throws SQLException if a database access error occurs
     */
    public static List<Student> list(DataSource dataSource) throws SQLException {
        return new QueryRunner(dataSource).query("SELECT * FROM Students", rs -> {
            List<Student> studentList = new ArrayList<>();
            while (rs.next()) {
                Student s = new Student(dataSource);
                s.studentId = rs.getString("studentid");
                s.firstName = rs.getString("firstname");
                s.middleName = rs.getString("middlename");
                s.lastName = rs.getString("lastname");
                s.email = rs.getString("email");
                studentList.add(s);
            }
            return studentList;
        });
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

}