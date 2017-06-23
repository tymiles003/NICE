package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Course in the database.
 */
@EqualsAndHashCode
public class Course {

    private final transient DataSource dataSource;

    /**
     * Database key, which may differ in state from crn
     */
    private transient Integer key;

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
        if (getKey() == null) {
            setKey(crn);
        }
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
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
     * Insert this course into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void insert() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "INSERT INTO Courses VALUES (?, ?, ?, ?)";
        if (run.update(sql, getCrn(), getName(), getNumber(), getSection()) != 1) {
            throw new SQLException("Insert failed.");
        }
        setKey(getCrn());
    }

    /**
     * Update this course in its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void update() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "UPDATE Courses SET crn = ?, name = ?, number = ?, section = ? "
                + "WHERE crn = ?";
        if (run.update(sql, getCrn(), getName(), getNumber(), getSection(), getKey()) != 1) {
            throw new SQLException("Update failed.");
        }
        setKey(getCrn());
    }

    /**
     * Delete this course from its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void delete() throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);
        String sql = "DELETE FROM Courses WHERE crn = ?";
        if (run.update(sql, getKey()) != 1) {
            throw new SQLException("Delete failed.");
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s-%02d)", getName(), getNumber(), getSection());
    }

}
