package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;

import java.sql.SQLException;

/**
 * Course in the database.
 */
@EqualsAndHashCode(callSuper = false)
@IdName("crn")
public class Course extends Model {

    private Integer crn;
    private String name;
    private String number;
    private Integer section;

    public Integer getCrn() {
        return isFrozen() ? crn : getInteger("crn");
    }

    public void setCrn(int crn) {
        setInteger("crn", crn);
    }

    public String getName() {
        return isFrozen() ? name : getString("name");
    }

    public void setName(String name) {
        setString("name", name);
    }

    public String getNumber() {
        return isFrozen() ? number : getString("number");
    }

    public void setNumber(String number) {
        setString("number", number);
    }

    public Integer getSection() {
        return isFrozen() ? section : getInteger("section");
    }

    public void setSection(int section) {
        setString("section", section);
    }

    /**
     * Insert this course into its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedInsert() throws SQLException {
        if (!insert()) {
            throw new SQLException("Insert failed.");
        }
    }

    /**
     * Update this course in its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedUpdate() throws SQLException {
        if (!saveIt()) {
            throw new SQLException("Update failed.");
        }
    }

    /**
     * Delete this course from its data source.
     *
     * @throws SQLException if a database access error occurs
     */
    public void checkedDelete() throws SQLException {
        this.crn = getCrn();
        this.name = getName();
        this.number = getNumber();
        this.section = getSection();
        if (!delete()) {
            throw new SQLException("Delete failed.");
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s-%02d)", getName(), getNumber(), getSection());
    }

}
