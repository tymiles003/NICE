package com.sudicode.nice.database;

import com.google.inject.Inject;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for courses.
 */
public class CourseDAO {

    private final DataSource dataSource;

    /**
     * Construct a new {@link CourseDAO}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    @Inject
    public CourseDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return A {@link Course} with the {@link DataSource} initialized.
     */
    public Course newCourse() {
        return new Course(dataSource);
    }

    /**
     * Obtain a list of {@link Course Courses}.
     *
     * @return A list of all courses
     * @throws SQLException if a database access error occurs
     */
    public List<Course> list() throws SQLException {
        return new QueryRunner(dataSource).query("SELECT * FROM Courses", rs -> {
            List<Course> courseList = new ArrayList<>();
            while (rs.next()) {
                Course c = newCourse();
                c.setCrn(rs.getInt("crn"));
                c.setName(rs.getString("name"));
                c.setNumber(rs.getString("number"));
                c.setSection(rs.getInt("section"));
                courseList.add(c);
            }
            return courseList;
        });
    }

}
