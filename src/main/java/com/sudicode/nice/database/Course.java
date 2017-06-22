package com.sudicode.nice.database;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Model class for courses.
 */
public class Course {

    private final DataSource dataSource;

    private int crn;
    private String name;
    private String number;
    private int section;

    /**
     * Constructor.
     *
     * @param dataSource The {@link DataSource} that this course originates from
     */
    Course(DataSource dataSource) {
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

    /**
     * Save ("upsert") this course into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void save() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "INSERT INTO Courses VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = ?, number = ?, section = ?";
        run.update(sql, getCrn(), getName(), getNumber(), getSection(),
                getName(), getNumber(), getSection());
    }

    @Override
    public String toString() {
        return String.format("%s (%s-%02d)", getName(), getNumber(), getSection());
    }

}
