package com.sudicode.nice.database;

import com.google.inject.Inject;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for students.
 */
public class StudentDAO {

    private final DataSource dataSource;

    /**
     * Construct a new {@link StudentDAO}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    @Inject
    public StudentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return A new {@link Student}.
     */
    public Student newStudent() {
        return new Student(dataSource);
    }

    /**
     * Obtain a list of {@link Student Students}.
     *
     * @return A list of all students
     * @throws SQLException if a database access error occurs
     */
    public List<Student> list() throws SQLException {
        return new QueryRunner(dataSource).query(
                "SELECT * FROM Students",
                rs -> {
                    List<Student> studentList = new ArrayList<>();
                    while (rs.next()) {
                        studentList.add(buildStudent(rs));
                    }
                    return studentList;
                });
    }

    /**
     * Obtain a list of students who are enrolled in a course.
     *
     * @param crn Course registration number
     * @return List of students
     * @throws SQLException if a database access error occurs
     */
    public List<Student> list(int crn) throws SQLException {
        return new QueryRunner(dataSource).query(
                "SELECT studentid FROM Registrations WHERE crn = ?",
                rs -> {
                    List<Student> studentList = new ArrayList<>();
                    while (rs.next()) {
                        getById(rs.getInt("studentid")).ifPresent(studentList::add);
                    }
                    return studentList;
                }, crn
        );
    }

    /**
     * Obtain a single {@link Student} by ID.
     *
     * @param studentId The student ID
     * @return An {@link Optional} which contains the student (if one exists with the given ID)
     * @throws SQLException if a database access error occurs
     */
    public Optional<Student> getById(int studentId) throws SQLException {
        return Optional.ofNullable(
                new QueryRunner(dataSource).query(
                        "SELECT * FROM Students WHERE studentId = ?",
                        rs -> rs.next() ? buildStudent(rs) : null,
                        studentId)
        );
    }

    /**
     * Builds a student from a {@link ResultSet} based on its current cursor position. This method does not change
     * the position of the cursor, i.e. <code>rs.next()</code> will not be called.
     *
     * @param rs The {@link ResultSet}
     * @return The {@link Student}
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    private Student buildStudent(ResultSet rs) throws SQLException {
        Student s = newStudent();
        s.setStudentId(rs.getInt("studentid"));
        s.setFirstName(rs.getString("firstname"));
        s.setMiddleName(rs.getString("middlename"));
        s.setLastName(rs.getString("lastname"));
        s.setEmail(rs.getString("email"));
        return s;
    }

}
