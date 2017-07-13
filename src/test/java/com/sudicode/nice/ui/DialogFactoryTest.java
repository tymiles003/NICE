package com.sudicode.nice.ui;

import com.diffplug.common.base.Errors;
import com.sudicode.nice.Constants;
import com.sudicode.nice.TestUtil;
import com.sudicode.nice.database.Course;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCode.TAB;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link DialogFactory}.
 */
public class DialogFactoryTest extends ApplicationTest {

    private static AtomicInteger counter;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        counter = new AtomicInteger();
        TestUtil.openDbConnection();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestUtil.closeDbConnection();
    }

    @Override
    public void start(Stage stage) {
        // Not necessary to show the stage, since we're only working with dialogs
    }

    @Test
    public void testInsertCourse() throws Exception {
        // Use countdown latch to prevent race conditions
        CountDownLatch latch = new CountDownLatch(1);

        // Show dialog
        Course course = new Course();
        Platform.runLater(() -> DialogFactory.showCourseDialog(course).ifPresent(Errors.rethrow().wrap(crs -> {
            try {
                TestUtil.openDbConnection();
                crs.insert();
            } finally {
                TestUtil.closeDbConnection();
                latch.countDown();
            }
        })));
        sleep(3, SECONDS); // wait a few seconds for dialog to appear

        // Fill it in and submit
        write("Sample Course").push(TAB);
        write("CRS-123").push(TAB);
        write("01").push(TAB);
        write(String.valueOf(counter.incrementAndGet())).push(TAB);
        for (int i = 1; i <= Constants.DAYS_OF_WEEK; i++) {
            push(SPACE).push(TAB).push(TAB).write(String.format("%d:00 AM", i));
            push(TAB).push(TAB).write(String.format("%d:30 AM", i)).push(TAB);
        }
        push(ENTER);

        // Test course against database
        latch.await();
        course.refresh();
        assertEquals("Sample Course", course.getName());
        assertEquals("CRS-123", course.getNumber());
        assertEquals(1, (int) course.getSection());
        for (int i = 1; i <= Constants.DAYS_OF_WEEK; i++) {
            assertEquals(LocalTime.of(i, 0), course.getStart(DayOfWeek.of(i)));
            assertEquals(LocalTime.of(i, 30), course.getEnd(DayOfWeek.of(i)));
        }
    }

}