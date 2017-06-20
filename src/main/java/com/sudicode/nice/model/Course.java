package com.sudicode.nice.model;

import com.sudicode.nice.dao.Courses;

import javax.sql.DataSource;

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
     * Default constructor. Avoid calling this directly, as it does not initialize the {@link DataSource}. Use
     * {@link Courses#newInstance()} instead.
     * <p>
     * Left public to allow access from {@link org.apache.commons.dbutils.DbUtils DbUtils}.
     */
    public Course() {
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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
