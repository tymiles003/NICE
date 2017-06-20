package com.sudicode.nice.dao;

import com.google.inject.Inject;
import com.sudicode.nice.model.Course;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for courses.
 */
public class Courses {

    private final DataSource dataSource;

    /**
     * Construct a new {@link Courses}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    @Inject
    public Courses(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return A {@link Course} with the {@link DataSource} initialized.
     */
    public Course newInstance() {
        Course course = new Course();
        course.setDataSource(dataSource);
        return course;
    }

    /**
     * Obtain a list of {@link Course Courses}.
     *
     * @return A list of all courses
     * @throws SQLException if a database access error occurs
     */
    public List<Course> list() throws SQLException {
        List<Course> list = new QueryRunner(dataSource).query("SELECT * FROM Courses", new BeanListHandler<>(Course.class));
        list.forEach(course -> course.setDataSource(dataSource));
        return list;
    }

}
