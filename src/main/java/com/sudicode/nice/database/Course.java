package com.sudicode.nice.database;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

/**
 * Course in the database.
 */
@EqualsAndHashCode(callSuper = false)
@IdName("crn")
@Table("Courses")
public class Course extends Model {

    private static final ImmutableMap<DayOfWeek, Character> DAYS = new ImmutableMap.Builder<DayOfWeek, Character>()
            .put(MONDAY, 'm')
            .put(TUESDAY, 't')
            .put(WEDNESDAY, 'w')
            .put(THURSDAY, 'r')
            .put(FRIDAY, 'f')
            .put(SATURDAY, 's')
            .put(SUNDAY, 'u')
            .build();

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
     * Get the course's start time on the given day of the week.
     *
     * @param dayOfWeek The {@link DayOfWeek}
     * @return The start time
     */
    public LocalTime getStart(DayOfWeek dayOfWeek) {
        return getStartOrEnd(dayOfWeek, "start");
    }

    /**
     * Set the course's start time on the given day of the week.
     *
     * @param dayOfWeek The {@link DayOfWeek}
     * @param time      The start time
     */
    public void setStart(DayOfWeek dayOfWeek, LocalTime time) {
        setStartOrEnd(dayOfWeek, time, "start");
    }

    /**
     * Get the course's end time on the given day of the week.
     *
     * @param dayOfWeek The {@link DayOfWeek}
     * @return The end time
     */
    public LocalTime getEnd(DayOfWeek dayOfWeek) {
        return getStartOrEnd(dayOfWeek, "end");
    }

    /**
     * Set the course's end time on the given day of the week.
     *
     * @param dayOfWeek The {@link DayOfWeek}
     * @param time      The end time
     */
    public void setEnd(DayOfWeek dayOfWeek, LocalTime time) {
        setStartOrEnd(dayOfWeek, time, "end");
    }

    /**
     * Get this course's start or end time, depending on the value of <code>startOrEnd</code>.
     *
     * @param dayOfWeek  The {@link DayOfWeek}
     * @param startOrEnd "start" or "end"
     * @return The start or end time
     */
    private LocalTime getStartOrEnd(DayOfWeek dayOfWeek, String startOrEnd) {
        Time time = getTime(DAYS.get(dayOfWeek) + "_" + startOrEnd);
        return time != null ? time.toLocalTime() : null;
    }

    /**
     * Set this course's start or end time, depending on the value of <code>startOrEnd</code>.
     *
     * @param dayOfWeek  The {@link DayOfWeek}
     * @param time       The start or end time
     * @param startOrEnd "start" or "end"
     */
    private void setStartOrEnd(DayOfWeek dayOfWeek, LocalTime time, String startOrEnd) {
        setTime(DAYS.get(dayOfWeek) + "_" + startOrEnd, time != null ? Time.valueOf(time) : null);
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
