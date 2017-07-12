package com.sudicode.nice.database;

import com.sudicode.nice.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Student}.
 */
public class StudentTest {

    private static AtomicInteger counter;

    private LocalDateTime now;
    private Student student;
    private Course course;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        counter = new AtomicInteger();
        TestUtil.openDbConnection();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestUtil.closeDbConnection();
    }

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();

        student = new Student();
        student.setId(counter.incrementAndGet());
        student.insert();

        course = new Course();
        course.setId(counter.incrementAndGet());
        course.insert();
    }

    @Test
    public void testPresent() throws Exception {
        TestUtil.setScheduleForEachDay(course, now.minusHours(1).toLocalTime(), now.plusHours(1).toLocalTime());
        student.enroll(course);
        student.attend(course);
        assertEquals("present", student.getStatus(course));
    }

    @Test
    public void testLate() throws Exception {
        TestUtil.setScheduleForEachDay(course, now.minusHours(1).toLocalTime(), now.minusHours(1).toLocalTime());
        student.enroll(course);
        student.attend(course);
        assertEquals("late", student.getStatus(course));
    }

    @Test
    public void testNoClass() throws Exception {
        student.enroll(course);
        assertEquals("no class", student.getStatus(course));
    }

    @Test
    public void testAbsent() throws Exception {
        TestUtil.setScheduleForEachDay(course, now.minusHours(1).toLocalTime(), now.minusHours(1).toLocalTime());
        student.enroll(course);
        assertEquals("absent", student.getStatus(course));
    }

}