package com.sudicode.nice.model;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for courses.
 */
public class Course {

    private DataSource dataSource;

    private int crn;
    private String name;
    private String number;
    private int section;

    /**
     * Construct a new {@link Course}.
     *
     * @param dataSource The {@link DataSource} to access
     */
    private Course(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Obtain a list of {@link Course Courses}.
     *
     * @param dataSource The {@link DataSource} to access
     * @return A list of all courses
     * @throws SQLException if a database access error occurs
     */
    public static List<Course> list(DataSource dataSource) throws SQLException {
        return new QueryRunner(dataSource).query("SELECT * FROM Courses", rs -> {
            List<Course> courseList = new ArrayList<>();
            while (rs.next()) {
                Course c = new Course(dataSource);
                c.setCrn(rs.getInt("crn"));
                c.setName(rs.getString("name"));
                c.setNumber(rs.getString("number"));
                c.setSection(rs.getInt("section"));
                courseList.add(c);
            }
            return courseList;
        });
    }

    public int getCrn() {
        return crn;
    }

    public void setCrn(int crn) {
        this.crn = crn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return String.format("%s (%s-%02d)", getName(), getNumber(), getSection());
    }

}
