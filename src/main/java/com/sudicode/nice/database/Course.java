package com.sudicode.nice.database;

import lombok.EqualsAndHashCode;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Time;
import java.time.LocalTime;

/**
 * Course in the database.
 */
@EqualsAndHashCode(callSuper = false)
@IdName("crn")
@Table("Courses")
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

    public LocalTime getMondayStart() {
        Time time = getTime("m_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setMondayStart(LocalTime time) {
        setTime("m_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getMondayEnd() {
        Time time = getTime("m_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setMondayEnd(LocalTime time) {
        setTime("m_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getTuesdayStart() {
        Time time = getTime("t_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setTuesdayStart(LocalTime time) {
        setTime("t_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getTuesdayEnd() {
        Time time = getTime("t_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setTuesdayEnd(LocalTime time) {
        setTime("t_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getWednesdayStart() {
        Time time = getTime("w_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setWednesdayStart(LocalTime time) {
        setTime("w_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getWednesdayEnd() {
        Time time = getTime("w_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setWednesdayEnd(LocalTime time) {
        setTime("w_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getThursdayStart() {
        Time time = getTime("r_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setThursdayStart(LocalTime time) {
        setTime("r_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getThursdayEnd() {
        Time time = getTime("r_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setThursdayEnd(LocalTime time) {
        setTime("r_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getFridayStart() {
        Time time = getTime("f_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setFridayStart(LocalTime time) {
        setTime("f_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getFridayEnd() {
        Time time = getTime("f_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setFridayEnd(LocalTime time) {
        setTime("f_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getSaturdayStart() {
        Time time = getTime("s_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setSaturdayStart(LocalTime time) {
        setTime("s_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getSaturdayEnd() {
        Time time = getTime("s_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setSaturdayEnd(LocalTime time) {
        setTime("s_end", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getSundayStart() {
        Time time = getTime("u_start");
        return time != null ? time.toLocalTime() : null;
    }

    public void setSundayStart(LocalTime time) {
        setTime("u_start", time != null ? Time.valueOf(time) : null);
    }

    public LocalTime getSundayEnd() {
        Time time = getTime("u_end");
        return time != null ? time.toLocalTime() : null;
    }

    public void setSundayEnd(LocalTime time) {
        setTime("u_end", time != null ? Time.valueOf(time) : null);
    }

    /**
     * Delete this course from its data source.
     */
    public void deleteIt() {
        this.crn = getCrn();
        this.name = getName();
        this.number = getNumber();
        this.section = getSection();
        delete();
    }

    @Override
    public String toString() {
        return String.format("%s (%s-%02d)", getName(), getNumber(), getSection());
    }

}
